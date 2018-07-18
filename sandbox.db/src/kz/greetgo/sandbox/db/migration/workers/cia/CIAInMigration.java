package kz.greetgo.sandbox.db.migration.workers.cia;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Bean
public class CIAInMigration {

  CIAInMigrationWorker cia;
  private Connection connection;

  public CIAInMigration(Connection connection) {

    this.connection = connection;
  }

  public void prepareWorker() {
    cia = new CIAInMigrationWorker(connection);
  }

  public void createTempTables() throws SQLException {
    cia.createTempTables();
  }

  public void insertTempClientsToReal() throws SQLException {
    cia.insertIntoClient();
  }

  public void insertTempAddressToReal() throws SQLException {
    cia.insertIntoAddress();
  }

  public void insertTempPhone() throws SQLException {
    cia.insertIntoPhone();
  }

  public void sendClient(List<ClientFromMigration> client) throws SQLException {
    cia.sendClient(client);
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    cia.sendPhones(phones);
  }

  public void sendAddresses(List<AddressFromMigration> addresses) throws SQLException {
    cia.sendAddresses(addresses);
  }

  public void updateError() throws SQLException, IOException {
    cia.updateError();
  }

  public void dropTempTables() throws SQLException {
    cia.dropTempTables();
  }

  public void closeConnection() throws SQLException {
    connection.close();
  }
}
