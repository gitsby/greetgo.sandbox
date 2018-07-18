package kz.greetgo.sandbox.db.migration.workers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

  public Connection connection;

  public void connect() throws Exception {
    connection = getConnection();

    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setAutoCommit(false);
  }

  private Connection getConnection() throws Exception {
    // TODO: Change to params
    String url = "jdbc:postgresql://localhost:5432/kayne_sandbox";
    Properties properties = new Properties();
    properties.setProperty("user", "kayne_sandbox");
    properties.setProperty("password", "111");
    return DriverManager.getConnection(url, properties);
  }


  public void closeConnection() throws SQLException {
    connection.close();
  }
}
