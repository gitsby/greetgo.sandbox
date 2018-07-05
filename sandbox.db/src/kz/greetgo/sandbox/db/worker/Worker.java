package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import org.postgresql.copy.CopyManager;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class Worker implements WorkerInterface {

  public List<Connection> connections;
  public InputStream inputStream;
  public MigrationConfig migrationConfig;
  public final String TMP_DIR = "build/tmp/";


  public Worker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    this.connections = connections;
    this.inputStream = inputStream;
    this.migrationConfig = migrationConfig;
  }

  public final void execute() throws SQLException, IOException, SAXException {
    createTmpTables();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmp();
    fuseTmpTables();
    validateTmpTables();
    migrateToTables();
    deleteTmpTables();
    finish();
  }

  public void copy(CopyManager copyManager, File file, String tmp) throws IOException, SQLException {
    //language=PostgreSQL
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    FileReader reader = new FileReader(file);
    copyManager.copyIn(r(copyQuery, tmp), reader);
    reader.close();
    file.delete();
  }

  public String checkStr(String str, Long counter) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) {
      if (counter == null) return str;
      else return counter + "#" + str;
    }
    return "\\N";
  }

  public String getTmpTableName(String tableName) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    return tableName+"_"+sdf.format(nowDate);
  }

  public Writer getWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    BufferedWriter bw = new BufferedWriter(osw, 10_000);
    return new PrintWriter(bw, true);
  }

  public File createFile(String path) throws IOException {
    File file = new File(path);
    if (!file.exists()) { new File(file.getParent()).mkdirs(); }
    file.createNewFile();
    return file;
  }

  public static CIAWorker getCiaWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) throws SAXException {
    return new CIAWorker(connections, inputStream, migrationConfig);
  }

  public static FRSWorker getFrsWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    return new FRSWorker(connections, inputStream, migrationConfig);
  }

  public void exec(String sql, String tmp) {
    String executingSql = r(sql, tmp);
    try (Statement statement = nextConnection().createStatement()) {
      statement.execute(executingSql);
    } catch (SQLException e) {
      System.out.println(e);
    }
  }

  private static int last = 0;

  public Connection nextConnection() {
    return connections.get(last % connections.size());
  }

  public String r(String sql, String tmp) {
    sql = sql.replaceAll("TMP_TABLE", tmp);
    return sql;
  }

  public void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
