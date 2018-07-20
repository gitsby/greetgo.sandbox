package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import org.apache.log4j.Logger;
import org.fest.util.Files;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Worker implements WorkerInterface {

  private static Logger logger = Logger.getLogger("migration");

  public static final double GIG = 1_000_000_000.0;

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

  public final File execute() {
    logger.info("----- EXECUTING -----");
    long start = System.nanoTime();
    fillTmpTables();
    margeTmpTables();
    validTmpTables();
    migrateTmpTables();
    long end = System.nanoTime();
    Calendar c = new GregorianCalendar();
    c.setTime(new Date(end-start));
    logger.info(String.format("----- FINISH AT: %s -----", showTime(end, start)));
    File error = writeOutErrorData();
    deleteTmpTables();
    return error;
  }

  protected void copy(CopyManager copyManager, File file, String tmp) {
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    try(FileReader reader = new FileReader(file)) {
      copyManager.copyIn(r(copyQuery, tmp), reader);
    } catch (IOException | SQLException e) {
      logger.error(e);
    }
    Files.delete(file);
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
    if (!file.exists())
      //noinspection ResultOfMethodCallIgnored
      new File(file.getParent()).mkdirs();
    //noinspection ResultOfMethodCallIgnored
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

  private String r(String sql, String tmp) {
    sql = sql.replaceAll("TMP_TABLE", tmp);
    return sql;
  }

  protected String checkIsNull(String str) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) {
      if ("null".equals(str.toLowerCase())) return "\\N";
      return str;
    }
    return "\\N";
  }

  protected File getFile(String name) {
    File newFile = new File("build/out_files/"+name);
    if (!newFile.exists())
      //noinspection ResultOfMethodCallIgnored
      newFile.getParentFile().mkdirs();
    try {
      //noinspection ResultOfMethodCallIgnored
      newFile.createNewFile();
    } catch (IOException e) {
      logger.error(e);
    }
    return newFile;
  }

  protected void copyOut(CopyManager copyManager, String tmp, Writer writer) {
    try {
      copyManager.copyOut(r("COPY (SELECT * FROM TMP_TABLE WHERE error IS NOT NULL) TO STDOUT WITH NULL ''", tmp), writer);
    } catch (SQLException | IOException e) {
      logger.error(e);
    }
  }

  protected void dropTmpTable(String tmpTable) {
    if (tmpTable == null) return;
    exec("DROP TABLE IF EXISTS TMP_TABLE", tmpTable);
  }

  protected static String recordsPerSecond(long recordCount, long periodInNano) {
    return formatDecimal((double) recordCount / (double) periodInNano * GIG) + " rec/s";
  }

  private static String formatDecimal(double decimal) {
    DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
    unusualSymbols.setDecimalSeparator('.');
    unusualSymbols.setGroupingSeparator(' ');

    String strange = "#,##0.000000";
    DecimalFormat weirdFormatter = new DecimalFormat(strange, unusualSymbols);
    weirdFormatter.setGroupingSize(3);

    return weirdFormatter.format(decimal);
  }

  protected String showTime(long nowNano, long pastNano) {
    return formatDecimal((double) (nowNano - pastNano) / GIG) + " s";
  }

  protected Thread getTimer(AtomicBoolean working, AtomicBoolean showStatus) {
    return new Thread(() -> {
      while (working.get()) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          break;
        }
        showStatus.set(true);
      }
    });
  }

  protected CopyManager getCopyManager() {
    try {
      return new CopyManager((BaseConnection) connection);
    } catch (SQLException e) {
      logger.error(e);
    }
    return null;
  }

  protected void closeWriter(Writer writer) {
    if (writer == null) return;
    try {
      writer.close();
    } catch (IOException e) {
      logger.error(e);
    }
  }

  protected void deleteFile(File file) {
    if (file == null) return;
    if (!file.exists()) return;
    Files.delete(file);
  }
}
