package kz.greetgo.sandbox.db.migration.workers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

  public Connection connection;

  public void connect() throws Exception {
    System.out.println("---------Starting Connection------------");
    connection = getConnection();

    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setAutoCommit(false);

    System.out.println("--Success connection--");
    try {
    } catch (Exception e) {
      e.printStackTrace();
      e.getCause();
    }
  }


  private Connection getConnection() throws Exception {
    // TODO: Change to params

    String[] connectionParams = getConnectionParams();
    String url = "jdbc:postgresql://localhost:5432/kayne_sandbox";
    Properties properties = new Properties();
    properties.setProperty("user", "kayne_sandbox");
    properties.setProperty("passworrd", "111");
    return DriverManager.getConnection(url, properties);
  }

  private String[] getConnectionParams() throws IOException {
    // TODO: Change path
//    BufferedReader bufferedReader = new BufferedReader(new FileReader("sandbox.db\\src\\kz\\greetgo\\sandbox\\db\\migration\\migration_conf.txt"));
    String[] params = new String[3];
//    String param;
//    int i = 0;
//    while ((param = bufferedReader.readLine()) != null) {
//      System.out.println(param);
//      if (i == 0) {
//        params[0] = param;
//        i++;
//        continue;
//      }
//      param = param.replace("user:", "");
//      param = param.replace("password:", "");
//      params[i++] = param;
//    }
    return params;
  }

  public void closeConnection() throws SQLException {
    connection.close();
  }
}
