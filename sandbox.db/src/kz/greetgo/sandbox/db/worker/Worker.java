package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import org.apache.log4j.Logger;
import org.postgresql.copy.CopyManager;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class Worker implements WorkerInterface {

  private static Logger logger = Logger.getLogger(Worker.class);

  protected Connection connection;
  protected InputStream inputStream;


  public Worker(Connection connection, InputStream inputStream) {
    this.connection = connection;
    this.inputStream = inputStream;
  }

  public static CIAWorker getCiaWorker(Connection connection, InputStream inputStream) {
    return new CIAWorker(connection, inputStream);
  }

  public static FRSWorker getFrsWorker(Connection connection, InputStream inputStream) {
    return new FRSWorker(connection, inputStream);
  }

  public final void execute() throws SQLException, IOException, SAXException {
    logger.info("----- EXECUTING -----");
    long start = System.nanoTime();
    createTmpTables();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmpTables();
    fuseMainTmpTables();
    validateMainTmpTables();
    migrateMainTmpTableToTables();
    fuseChildTmpTables();
    validateChildTmpTables();
    migrateChildTmpTablesToTables();
    deleteTmpTables();
    finish();
    long end = System.nanoTime();
    Calendar c = new GregorianCalendar();
    c.setTime(new Date(end-start));
    logger.info(String.format("----- FINISH AT: %d n/s -----", end-start));
  }

  protected void copy(CopyManager copyManager, File file, String tmp) throws IOException, SQLException {
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    FileReader reader = new FileReader(file);
    copyManager.copyIn(r(copyQuery, tmp), reader);
    reader.close();
    file.delete();
  }

  protected String getTmpTableName(String tableName) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    return tableName+"_"+sdf.format(nowDate);
  }

  protected boolean isDate(String date) {
    if (date==null) return false;
    return date.matches("^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$");
  }

  protected Writer getWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    BufferedWriter bw = new BufferedWriter(osw, 100_000);
    return new PrintWriter(bw, true);
  }

  protected File createFile(String name) throws IOException {
    String tmp = "build/tmp/";
    File file = new File(tmp+name);
    if (!file.exists()) { new File(file.getParent()).mkdirs(); }
    file.createNewFile();
    return file;
  }

  protected void exec(String sql, String tmp) {
    String executingSql = r(sql, tmp);
    try (Statement statement = connection.createStatement()) {
      statement.execute(executingSql);
    } catch (SQLException e) {
      logger.error(e.getMessage());
    }
  }

  private String r(String sql, String tmp) {
    sql = sql.replaceAll("TMP_TABLE", tmp);
    return sql;
  }

  protected void deleteTable(String tmp) {
    exec("DROP TABLE IF EXISTS TMP_TABLE", tmp);
    exec("DROP TABLE IF EXISTS TMP_TABLE_conductor", tmp);
  }

  protected String checkStr(String str, Long counter) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) {
      if (str.toLowerCase().equals("null")) return "\\N";
      if (counter == null) return str;
      else return counter + "#" + str;
    }
    return "\\N";
  }
}
