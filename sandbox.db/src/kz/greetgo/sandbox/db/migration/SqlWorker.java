package kz.greetgo.sandbox.db.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlWorker {

  Connection connection;

  public SqlWorker(Connection connection) {
    this.connection = connection;
  }

  void exec(String query, Object... params) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    int i = 1;
    for (Object param : params) {
      preparedStatement.setObject(i++, param);
    }
    preparedStatement.executeUpdate();
  }
}
