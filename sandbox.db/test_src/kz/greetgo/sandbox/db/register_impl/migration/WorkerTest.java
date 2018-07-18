package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.test.dao.MigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.worker.Worker;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class WorkerTest extends ParentTestNg {

  private static final Logger logger = Logger.getLogger("test");
  private static final String DIR = "build/out_files/";

  public BeanGetter<MigrationTestDao> migrationDao;
  public BeanGetter<DbConfig> dbConf;
  public BeanGetter<Migration> migration;

  CIAWorker getCiaWorker(Connection connection, InputStream inputStream) {
    return Worker.getCiaWorker(connection, inputStream);
  }

  FRSWorker getFrsWorker(Connection connection, InputStream inputStream) {
    return Worker.getFrsWorker(connection, inputStream);
  }

  Connection getConnection() {
    try {
      return DriverManager.getConnection(dbConf.get().url(), dbConf.get().username(), dbConf.get().password());
    } catch (SQLException e) {
      logger.error(e);
    }
    return null;
  }

  List<String> getFrsTmpTableNames() {
    return migrationDao.get().getTablesName().stream().filter(name -> name.startsWith("frs_migration")).collect(Collectors.toList());
  }

  List<String> getCiaTmpTableNames() {
    return migrationDao.get().getTablesName().stream().filter(name -> name.startsWith("cia_migration")).collect(Collectors.toList());
  }

  InputStream getInputStream(String fileName, String str) throws Exception {
    return new FileInputStream(createTmpFile(fileName, str));
  }

  String getRandomDate(String dateFormat) {
    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
    return format.format(new Date());
  }

  String getNameWithDate(String name) {
    return name + "_" + getRandomDate("yyyyMMdd_HHmmss_S");
  }

  void removeTmpTables(List<String> tmpTableNames) {
    for (String tmpTableName : tmpTableNames) migrationDao.get().removeTable(tmpTableName);
  }

  File createTmpFile(String name, String text) throws Exception {
    File file = new File(DIR + name);
    if (!file.exists()) file.getParentFile().mkdirs();
    try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
      out.println(text);
    }
    return file;
  }
}
