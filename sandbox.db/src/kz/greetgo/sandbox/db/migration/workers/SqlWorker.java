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

  public void batchInsert(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      statement.setObject(i + 1, params[i]);
    }
    statement.addBatch();
  }

  public Timestamp timeStampFromString(String date) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd hh:mm:ss.SSS");

    Date parsedTimeStamp = dateFormat.parse(date);

    Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());

    return timestamp;
  }

  public java.sql.Date formatDate(String birth) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date date = format.parse(birth);
      return new java.sql.Date(date.getTime());
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isValidFormat(String format, String value) {
    SimpleDateFormat form = new SimpleDateFormat(format);
    Date currentDate = new Date();

    try {
      Date birthDate = form.parse(value);
      int diffYears = DateHelper.calculateAge(DateHelper.toLocalDate(birthDate), DateHelper.toLocalDate(currentDate));

      return ((3 < diffYears) && (diffYears < 1000));
    } catch (Exception e) {
    }

    return false;
  }


}
