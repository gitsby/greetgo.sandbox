package kz.greetgo.sandbox.db.migration;

import com.impossibl.postgres.api.jdbc.PGConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class SqlWorker {

  Connection connection;

  public SqlWorker(Connection connection) {
    this.connection = connection;
  }

  void exec(String query, Object... params) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    int i = 1;
    for (Object param : params) {
      if (param instanceof Date) {
        preparedStatement.setObject(i++, java.sql.Date.valueOf("2013-09-04"));
      } else {

        preparedStatement.setObject(i++, param);
      }
    }
    preparedStatement.executeUpdate();
  }
}
