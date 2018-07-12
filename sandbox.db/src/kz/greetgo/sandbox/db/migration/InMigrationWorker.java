package kz.greetgo.sandbox.db.migration;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.util.Calendar.YEAR;

public class InMigrationWorker extends SqlWorker {

  Logger logger = Logger.getLogger("callback");

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
      "          client_id varchar(40) primary key,  \n" +
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
      "client_id varchar(40) primary key," +
      "street varchar(100)," +
      "flat varchar(100)," +
      "house varchar(100)," +
      "type varchar(10) primary key)");

    connection.commit();
  }

  public void updater() throws SQLException {
    exec("update temp_client as tmp set client_val_id=1;");
    exec("update temp_client\n" +
      "set client_val_id = 2 from (select\n" +
      "                              count(*),\n" +
      "                              client_id\n" +
      "                            from temp_client\n" +
      "                            group by client_id\n" +
      "                            having count(*) > 1) as counter\n" +
      "where temp_client.client_id = counter.client_id;");
    exec("update temp_client as tmp\n" +
      "set client_val_id = 3 from (select\n" +
      "                              min(created_at),\n" +
      "                              client_id\n" +
      "                            from temp_client\n" +
      "                            group by client_id\n" +
      "                            having count(*) > 1) as minTable\n" +
      "where tmp.client_id = minTable.client_id and tmp.created_at = minTable.min;");
    exec("update temp_client as tmp\n" +
      "set client_val_id = 4 from (select\n" +
      "                              max(created_at),\n" +
      "                              client_id\n" +
      "                            from temp_client\n" +
      "                            group by client_id\n" +
      "                            having count(*) > 1) as maxTable\n" +
      "where tmp.client_id = maxTable.client_id and tmp.created_at = maxTable.max;");
    exec("update temp_client as tmp\n" +
      "set client_val_id = 5 from (select\n" +
      "                              client_id,\n" +
      "                              created_at\n" +
      "                            from temp_client\n" +
      "                            where error notnull and error = '' and client_val_id = 4) as noError\n" +
      "where tmp.client_id = noError.client_id and tmp.created_at = noError.created_at;");

    connection.commit();
  }

  public void sendClient(List<ClientFromMigration> clients) throws SQLException {
    StringBuilder builder = new StringBuilder();
    List<Object> params = new LinkedList<>();
    for (int i = 0; i < clients.size(); i++) {
      ClientFromMigration client = clients.get(i);

      if (client.name == null) {
        client.error.append("Name is null; ");
      } else if (client.name.isEmpty()) {
        client.error.append("Name is empty; ");
      }
      if (client.surname == null) {
        client.error.append("Surname is null; ");
      } else if (client.surname.isEmpty()) {
        client.error.append("Surname is empty; ");
      }
      if (client.gender == null) {
        client.error.append("Gender is null; ");
      } else if (client.gender.isEmpty()) {
        client.error.append("Gender is empty; ");
      }
      params.add(client.id);
      params.add(client.name);
      params.add(client.surname);
      params.add(client.patronymic);
      params.add(client.gender);
      params.add(client.charm);

      params.add(client.error.toString());

      if (client.birth == null) {
        client.error.append("Birth date is null; ");
        params.add(null);
      } else if (!isValidFormat("yyyy-MM-dd", client.birth)) {
        client.error.append("Invalid birth date; ");
        params.add(null);
      } else {
        params.add(formatDate(client.birth));
      }
      builder.append(client.getInsertString());
      params.add(client.timestamp);
    }
    exec(builder.toString(), params.toArray());
    connection.commit();
  }

  private java.sql.Date formatDate(String birth) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date date = format.parse(birth);
      return new java.sql.Date(date.getTime());
    } catch (Exception e) {
      return null;
    }
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

    List<Object> params = new LinkedList<>();
    for (int i = 0; i < addressFromMigrations.size(); i++) {

      AddressFromMigration address = addressFromMigrations.get(i);
      builder.append(address.getInsertString());
      params.add(address.street);
      params.add(address.house);
      params.add(address.flat);
      params.add(address.type);
    }
    exec(builder.toString(), params.toArray());
    connection.commit();
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    StringBuilder builder = new StringBuilder();
    List<Object> params = new LinkedList<>();
    for (int i = 0; i < phones.size(); i++) {
      PhoneFromMigration phone = phones.get(i);

      builder.append(phone.getInsertString());
      params.add(phone.client_id);
      params.add(phone.number);
      params.add(phone.type);
    }

    exec(builder.toString(), params.toArray());
    connection.commit();
  }
}
