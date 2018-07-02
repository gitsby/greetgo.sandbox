package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.reader.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.PhoneFromMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.YEAR;

public class InMigrationWorker extends SqlWorker {

  public InMigrationWorker(Connection connection) {
    super(connection);
  }

  int triesNum = 0;

  public void prepare() throws SQLException {
//    exec("drop table temp_client");
//    System.out.println("DROPPED");
//    exec("drop table temp_phone");
//    exec("drop table temp_address");
    exec("create table temp_client ( \n" +
      "          id int,\n" +
      "          created_at timestamp not null ,\n" +
      "          client_id varchar(40),  \n" +
      "          name varchar(30), \n" +
      "          surname varchar(30), \n" +
      "          patronymic varchar(30), \n" +
      "          gender varchar(10), \n" +
      "          birth_date date, \n" +
      "          charm varchar(15), \n" +
      "          error text)");
    exec("create table temp_phone(" +
      "client_id varchar(40)," +
      "number varchar(30)," +
      "type varchar(10))");

    exec("create table temp_address(" +
      "client_id varchar(40)," +
      "street varchar(100)," +
      "flat varchar(100)," +
      "house varchar(100)," +
      "type varchar(10))");
    connection.commit();
    System.out.println("CREATED TABLES");
  }

  public void sendClient(List<ClientFromMigration> clients) throws SQLException {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < clients.size(); i++) {
      ClientFromMigration client = clients.get(i);

      StringBuilder error = new StringBuilder();

      if (client.name == null) {
        error.append("Name is null; ");
      } else if (client.name.isEmpty()) {
        error.append("Name is empty; ");
      }

      if (client.surname == null) {
        error.append("Surname is null; ");
      } else if (client.surname.isEmpty()) {
        error.append("Surname is empty; ");
      }

      if (client.gender == null) {
        error.append("Gender is null; ");
      } else if (client.gender.isEmpty()) {
        error.append("Gender is empty; ");
      }

      if (client.birth == null) {
        error.append("Birth date is null; ");
      } else if (!isValidFormat("yyyy-MM-dd", client.birth)) {
        error.append("Invalid birth date; ");
      }
      builder.append(client.toInsertString(error));
    }
    exec(builder.toString());
    connection.commit();
  }

  public static boolean isValidFormat(String format, String value) {
    SimpleDateFormat form = new SimpleDateFormat(format);
    Date currentDate = new Date();

    try {
      Date birthDate = form.parse(value);
      int diffYears = getDiffYears(birthDate, currentDate);

      return ((3 < diffYears) && (diffYears < 1000));
    } catch (Exception e) {
    }

    return false;
  }

  @SuppressWarnings("Duplicates")
  private static int getDiffYears(Date first, Date last) {
    Calendar a = getCalendar(first);
    Calendar b = getCalendar(last);
    int diffYears = b.get(YEAR) - a.get(YEAR);

    return diffYears;
  }

  private static Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }

  public void sendAddresses(List<AddressFromMigration> addressFromMigrations) throws SQLException {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < addressFromMigrations.size(); i++) {

      AddressFromMigration address = addressFromMigrations.get(i);
      builder.append(address.toString());
      if (i % 2 == 500) {
        exec(builder.toString());
        connection.commit();
      }
    }
    connection.commit();
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < phones.size(); i++) {
      PhoneFromMigration phone = phones.get(i);

      builder.append(phone.toInsertString());
      //exec("insert into temp_phone (client_id, number, type) values(?, ?, ?)", phone.client_id, phone.number, phone.type);
      if (i % 500 == 0) {
        exec(builder.toString());
        builder = new StringBuilder();
        connection.commit();
      }
    }
    System.out.println("FINISHED");
    connection.commit();
  }
}
