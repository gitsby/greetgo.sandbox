package kz.greetgo.sandbox.db.migration.workers;

import kz.greetgo.sandbox.db.helper.DateHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SqlWorker {

  public Connection connection;

  public SqlWorker(Connection connection) {
    this.connection = connection;
  }

  public void exec(String query, Object... params) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    int i = 1;
    for (Object param : params) {
      preparedStatement.setObject(i++, param);
    }
    preparedStatement.executeUpdate();
    preparedStatement.close();
  }

}
