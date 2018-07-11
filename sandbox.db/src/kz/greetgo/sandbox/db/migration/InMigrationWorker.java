package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.YEAR;

public class InMigrationWorker extends SqlWorker {

  public InMigrationWorker(Connection connection) {
    super(connection);
  }

  public void prepare() throws SQLException {
//    exec("drop table temp_client");
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
  }

  public void updater(){

  }

  public void sendClient(List<ClientFromMigration> clients) throws SQLException {
    StringBuilder builder = new StringBuilder();
    List params = new LinkedList();
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
      params.add(client.id);
      params.add(client.name);
      params.add(client.surname);
      params.add(client.patronymic);
      params.add(client.gender);
      params.add(client.charm);
      params.add(error.toString());
      builder.append(client.getInsertString());
    }
    exec(builder.toString(), params.toArray());
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

    List params = new LinkedList();
    for (int i = 0; i < addressFromMigrations.size(); i++) {

      AddressFromMigration address = addressFromMigrations.get(i);
      builder.append(address.getInsertString());
      params.add(address.street);
      params.add(address.house);
      params.add(address.flat);
      params.add(address.type);
      if (i % 500 == 0) {
        exec(builder.toString(), params.toArray());
        builder = new StringBuilder();
        params = new ArrayList();
        connection.commit();
      }
    }
    connection.commit();
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    StringBuilder builder = new StringBuilder();
    List params = new LinkedList();
    for (int i = 0; i < phones.size(); i++) {
      PhoneFromMigration phone = phones.get(i);

      builder.append(phone.getInsertString());
      params.add(phone.client_id);
      params.add(phone.number);
      params.add(phone.type);
      if (i % 500 == 0) {
        exec(builder.toString(), params.toArray());
        builder = new StringBuilder();
        params = new LinkedList();
        connection.commit();
      }
    }
    connection.commit();
  }
}
