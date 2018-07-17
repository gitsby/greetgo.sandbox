package kz.greetgo.sandbox.db.migration.workers.cia;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import kz.greetgo.sandbox.db.migration.workers.SqlWorker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CIAInMigrationWorker extends SqlWorker {

  public CIAInMigrationWorker(Connection connection) {
    super(connection);
  }

  public void createTempTables() throws SQLException {
    exec("create table temp_client ( \n" +
      "          created_at timestamp not null ,\n" +
      "          client_id varchar(40) primary key,  \n" +
      "          name varchar(30), \n" +
      "          surname varchar(30), \n" +
      "          patronymic varchar(30), \n" +
      "          gender varchar(10), \n" +
      "          birth_date date, \n" +
      "          charm varchar(15), \n" +
      "          error text);");

    exec("create table temp_phone(" +
      "client_id varchar(40)," +
      "number varchar(30)," +
      "type varchar(10));");

    exec("create table temp_address (\n" +
      "  client_id varchar(40),\n" +
      "  street    varchar(100),\n" +
      "  flat      varchar(100),\n" +
      "  house     varchar(100),\n" +
      "  type      varchar(10),\n" +
      "  PRIMARY KEY (client_id, type)\n" +
      ");");

    connection.commit();
  }

  public void insertIntoClient() throws SQLException {
    exec("alter table client\n" +
      "  add column migr_client_id varchar(40);\n");

    exec("insert into characters (name) select charm\n" +
      "                              from temp_client\n" +
      "                              where charm notnull\n" +
      "                              group by charm;");

    System.out.println("CHARS READY");

    exec("insert into\n" +
      "  client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
      "  select\n" +
      "    temp_client.name,\n" +
      "    temp_client.surname,\n" +
      "    temp_client.patronymic,\n" +
      "    temp_client.gender,\n" +
      "    temp_client.birth_date,\n" +
      "    characters.client_id,\n" +
      "    temp_client.client_id\n" +
      "  from temp_client, characters\n" +
      "  where temp_client.charm = characters.name and\n" +
      "        temp_client.name notnull and TRIM(temp_client.name) != '' and\n" +
      "        temp_client.surname notnull and TRIM(temp_client.surname) != '' and\n" +
      "        temp_client.birth_date notnull and\n" +
      "        temp_client.gender notnull and TRIM(temp_client.gender) != '';");

    System.out.println("CLIENTS READY");

    connection.commit();
  }

  public void insertIntoPhone() throws SQLException {
    exec("insert into client_phone (client_id, number, type)\n" +
      "  select\n" +
      "    client.client_id,\n" +
      "    temp_phone.number,\n" +
      "    temp_phone.type\n" +
      "  from client, temp_phone\n" +
      "  where client.migr_client_id = temp_phone.client_id;");
    System.out.println("PHONE READY");
    connection.commit();
  }

  public void insertIntoAddress() throws SQLException {
    exec("insert into client_address (client_id, type, street, house, flat)\n" +
      "  SELECT\n" +
      "    client.client_id,\n" +
      "    temp_address.type,\n" +
      "    temp_address.street,\n" +
      "    temp_address.house,\n" +
      "    temp_address.flat\n" +
      "  from client, temp_address\n" +
      "  where client.migr_client_id = temp_address.client_id;");
    System.out.println("ADDRESS READY");
    connection.commit();
  }


  public void dropTempTables() throws SQLException {

    //exec("alter table client drop column migr_client_id;");

//    exec("drop table temp_client");
//    exec("drop table temp_phone");
//    exec("drop table temp_address");
    //connection.commit();

  }

  public void sendClient(List<ClientFromMigration> clients) throws SQLException {
    StringBuilder builder = new StringBuilder();
    List<Object> params = new LinkedList<>();

    for (int i = 0; i < clients.size(); i++) {
      ClientFromMigration client = clients.get(i);

      params.add(client.client_id);
      params.add(client.name);
      params.add(client.surname);
      params.add(client.patronymic);
      params.add(client.gender);
      params.add(client.charm);
      params.add(client.error.toString());

      if (client.birth == null) {
        params.add(null);
      } else if (!isValidFormat("yyyy-MM-dd", client.birth)) {
        params.add(null);
      } else {
        params.add(formatDate(client.birth));
      }

      builder.append(client.getInsertString());
      params.add(client.timestamp);
    }

    exec(builder.toString(), params.toArray());
    connection.commit();
    System.out.println("Clients send!");
  }

  public void sendAddresses(List<AddressFromMigration> addressFromMigrations) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("insert into temp_address(client_id,street, house, flat, type) values(?,?,?,?,?) on conflict (client_id, type)\n" +
      "  do update\n" +
      "    set street = case when EXCLUDED.street notnull and EXCLUDED.street != ''\n" +
      "      then EXCLUDED.street end,\n" +
      "      flat = case when EXCLUDED.flat notnull and EXCLUDED.flat != ''\n" +
      "        then EXCLUDED.flat end,\n" +
      "      house = case when EXCLUDED.house notnull and EXCLUDED.house != ''\n" +
      "        then EXCLUDED.house end");
    for (int i = 0; i < addressFromMigrations.size(); i++) {

      AddressFromMigration address = addressFromMigrations.get(i);
      batchInsert(statement, address.client_id, address.street, address.house, address.flat, address.type);
      if (i % 500 == 0) {
        statement.executeBatch();
      }
    }
    statement.executeBatch();
    connection.commit();
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("insert into temp_phone (client_id, number, type) values(?,?,?)");
    for (int i = 0; i < phones.size(); i++) {
      PhoneFromMigration phone = phones.get(i);

      batchInsert(statement, phone.client_id, phone.number, phone.type);
      if (i % 500 == 0) {
        statement.executeBatch();
      }
    }
    statement.executeBatch();
    statement.close();
    connection.commit();
  }

}
