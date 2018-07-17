package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import org.apache.log4j.Logger;
import org.postgresql.copy.CopyManager;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

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

  public final File execute() throws SQLException, IOException {
    logger.info("----- EXECUTING -----");
    long start = System.nanoTime();
    fillTmpTables();
    margeTmpTables();
    validTmpTables();
    migrateTmpTables();
    long end = System.nanoTime();
    Calendar c = new GregorianCalendar();
    c.setTime(new Date(end-start));
    logger.info(String.format("----- FINISH AT: %d n/s -----", end-start));
    File error = getErrorInFile();
    deleteTmpTables();
    finish();
    return error;
  }

  protected void copy(CopyManager copyManager, File file, String tmp) {
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    try(FileReader reader = new FileReader(file)) {
      copyManager.copyIn(r(copyQuery, tmp), reader);
    } catch (IOException | SQLException e) {
      logger.error(e);
    }
    file.delete();
  }

  protected String getNameWithDate(String tableName) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_S");
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
      logger.error(e);
    }
  }

  protected String r(String sql, String tmp) {
    sql = sql.replaceAll("TMP_TABLE", tmp);
    return sql;
  }

  protected String checkIsNull(String str) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) {
      if (str.toLowerCase().equals("null")) return "\\N";
      return str;
    }
    return "\\N";
  }

  protected File getFile(String name) {
    File newFile = new File("build/out_files/"+name);
    if (!newFile.exists()) newFile.getParentFile().mkdirs();
    try {
      newFile.createNewFile();
    } catch (IOException e) {
      logger.error(e);
    }
    return newFile;
  }

  protected void parallelTasks(Runnable... runnableList) {
    for (Thread thread : startTasks(runnableList)) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected void backgroundTasks(Runnable... runnableList) {
    startTasks(runnableList);
  }

  private List<Thread> startTasks(Runnable[] runnableList) {
    List<Thread> threadList = new ArrayList<>();
    for (Runnable aRunnableList : runnableList) threadList.add(new Thread(aRunnableList));
    threadList.forEach(Thread::start);
    return threadList;
  }

  protected void copyOut(CopyManager copyManager, String tmp, Writer writer) {
    try {
      copyManager.copyOut(r("COPY (SELECT * FROM TMP_TABLE WHERE error IS NOT NULL) TO STDOUT WITH NULL ''", tmp), writer);
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
