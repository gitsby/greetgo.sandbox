package kz.greetgo.sandbox.db.migration.workers.cia;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import kz.greetgo.sandbox.db.migration.workers.SqlWorker;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CIAInMigrationWorker extends SqlWorker {

  public CIAInMigrationWorker(Connection connection) {
    super(connection);
  }

  public void createTempTables() throws SQLException {
    exec("create table if not exists temp_client ( \n" +
      "          created_at timestamp not null ,\n" +
      "          client_id varchar(40) primary key,  \n" +
      "          name varchar(30), \n" +
      "          surname varchar(30), \n" +
      "          patronymic varchar(30), \n" +
      "          gender varchar(10), \n" +
      "          birth_date date, \n" +
      "          charm varchar(15), \n" +
      "          error text);");

    exec("create table if not exists temp_phone(" +
      "client_id varchar(40)," +
      "number varchar(30)," +
      "type varchar(10));");

    exec("create table if not exists temp_address (\n" +
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
      "  add column if not exists migr_client_id varchar(40);\n");

    exec("insert into characters (name) select charm\n" +
      "                              from temp_client\n" +
      "                              where charm notnull\n" +
      "                              group by charm;");


    exec("insert into\n" +
      "  client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
      "  select\n" +
      "    temp_client.name,\n" +
      "    temp_client.surname,\n" +
      "    temp_client.patronymic,\n" +
      "    temp_client.gender,\n" +
      "    temp_client.birth_date,\n" +
      "    characters.id,\n" +
      "    temp_client.client_id\n" +
      "  from temp_client, characters\n" +
      "  where temp_client.charm = characters.name and error isnull");


    connection.commit();
  }

  public void insertIntoPhone() throws SQLException {
    exec("insert into client_phone (client_id, number, type)\n" +
      "  select\n" +
      "    client.id,\n" +
      "    temp_phone.number,\n" +
      "    temp_phone.type\n" +
      "  from client, temp_phone\n" +
      "  where client.migr_client_id = temp_phone.client_id;");
    connection.commit();
  }

  public void insertIntoAddress() throws SQLException {
    exec("insert into client_address (client_id, type, street, house, flat)\n" +
      "  SELECT\n" +
      "    client.id,\n" +
      "    temp_address.type,\n" +
      "    temp_address.street,\n" +
      "    temp_address.house,\n" +
      "    temp_address.flat\n" +
      "  from client, temp_address\n" +
      "  where client.migr_client_id = temp_address.client_id;");
    connection.commit();
  }


  public void dropTempTables() throws SQLException {

    exec("alter table client drop column migr_client_id;");

    exec("drop table temp_client");
    exec("drop table temp_phone");
    exec("drop table temp_address");
    connection.commit();

  }

  public void sendClient(List<ClientFromMigration> clients) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("insert into temp_client(client_id, name, surname, patronymic, gender, charm, birth_date,created_at)" +
      "values(?,?,?,?,?,?,?,?) on conflict (client_id)\n" +
      "  do update\n" +
      "    set\n" +
      "      surname    = case when EXCLUDED.surname notnull\n" +
      "        then EXCLUDED.surname end,\n" +
      "      name       = case when EXCLUDED.name notnull\n" +
      "        then EXCLUDED.name end,\n" +
      "      patronymic = EXCLUDED.patronymic,\n" +
      "      birth_date = case when EXCLUDED.birth_date notnull\n" +
      "        then EXCLUDED.birth_date end,\n" +
      "      gender     = case when EXCLUDED.gender notnull\n" +
      "        then EXCLUDED.gender end,\n" +
      "      charm      = case when EXCLUDED.charm notnull\n" +
      "        then EXCLUDED.charm end;");
    for (int i = 0; i < clients.size(); i++) {
      ClientFromMigration client = clients.get(i);

      Date date = null;

      if (isValidFormat("yyyy-MM-dd", client.birth)) {
        date = formatDate(client.birth);
      }

      batchInsert(statement, client.client_id, client.name, client.surname, client.patronymic, client.gender, client.charm, date, client.timestamp);
    }

    statement.executeBatch();
    connection.commit();
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

  public void updateError() throws SQLException, IOException {
    exec("update temp_client\n" +
      "set error = case\n" +
      "            when name isnull or TRIM(name) = ''\n" +
      "              then 'Invalid name;'\n" +
      "            when surname isnull or TRIM(surname) = ''\n" +
      "              then 'Invalid surname;'\n" +
      "            when birth_date isnull\n" +
      "              then 'Invalid birth date;'\n" +
      "            when charm isnull or TRIM(charm) = ''\n" +
      "              then 'Invalid charm;'\n" +
      "            when gender isnull or TRIM(gender) = ''\n" +
      "              then 'Invalid gender;'\n" +
      "            end;\n");
    CopyManager copyManager = new CopyManager((BaseConnection) connection);
    copyManager.copyOut("COPY (select * from temp_client where error!='' and error notnull) to STDOUT ", new PrintWriter("build/error.csv", "UTF-8"));

    connection.commit();
  }
}
