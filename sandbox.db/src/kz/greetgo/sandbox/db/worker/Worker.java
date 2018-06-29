package kz.greetgo.sandbox.db.worker;

import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class Worker {

  public List<Connection> connections;
  public InputStream inputStream;
  public MigrationConfig migrationConfig;
  public int cIndex = 0;

  public Worker(List<Connection> connection, InputStream inputStream, MigrationConfig migrationConfig) {
    this.connections = connection;
    this.inputStream = inputStream;
    this.migrationConfig = migrationConfig;
  }

  public abstract void createTables() throws SQLException;
  public abstract void startLoading() throws SQLException;
  public abstract void prepareStatements() throws SQLException;
  public abstract void checkBatch() throws SQLException;
  public abstract void finish() throws SQLException;

  public final void execute() throws SQLException {
    createTables();
    prepareStatements();
    startLoading();
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

  public String r(String sql, String tmp) {
    sql = sql.replaceAll("TMP_TABLE", tmp);
    return sql;
  }

  public void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }

  public Connection nextConnection() {
    return connections.get(cIndex++ % connections.size());
  }

  public void commitConnections() throws SQLException {
    for (int i = 0; i < connections.size(); i++) connections.get(i).commit();
  }

  public void setObjects(PreparedStatement ps, Object[] objects) throws SQLException {
    if (objects == null) return;
    for (int i = 0; i < objects.length; i ++) ps.setObject(i+1, objects[i]);
  }
}
