package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;

import java.io.IOException;
import java.io.InputStream;
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

  public Worker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    this.connections = connections;
    this.inputStream = inputStream;
    this.migrationConfig = migrationConfig;
  }

  public final void execute() throws SQLException, IOException {
    createTmpTables();
    prepareStatements();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmp();
    fuseTmpTables();
    validateTmpTables();
    migrateToTables();
    finish();
  }


  public static CIAWorker getCiaWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
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
