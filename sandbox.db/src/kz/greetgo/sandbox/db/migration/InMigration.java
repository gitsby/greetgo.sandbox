package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

@Bean
public class InMigration {

  InMigrationWorker inMigrationWorker;
  Connection connection;

  Logger logger = Logger.getLogger("callback");

  public boolean execute() throws Exception {
    System.out.println("---------Starting Connection------------");
    connection = getConnection();

    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    connection.setAutoCommit(false);
    System.out.println("--Success connection--");
    try {
      inMigrationWorker = new InMigrationWorker(connection);
      inMigrationWorker.prepare();
      return true;
    } catch (Exception e) {
      logger.debug(e.getMessage());
    }
    return false;
  }

  public void startUpdate() throws SQLException {
    inMigrationWorker.updater();
  }

  public void sendClient(List<ClientFromMigration> client) throws SQLException {
    inMigrationWorker.sendClient(client);
  }

  public void sendPhones(List<PhoneFromMigration> phones) throws SQLException {
    inMigrationWorker.sendPhones(phones);
  }

  public void sendAddresses(List<AddressFromMigration> addresses) throws SQLException {
    inMigrationWorker.sendAddresses(addresses);
  }

  private Connection getConnection() throws Exception {
    String[] connectionParams = getConnectionParams();
    String url = "jdbc:postgresql://localhost:5432/kayne_sandbox";
    Properties properties = new Properties();
    properties.setProperty("user", "kayne_sandbox");
    properties.setProperty("passworrd", "111");
    return DriverManager.getConnection(url, properties);
  }

  private String[] getConnectionParams() throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader("sandbox.db\\src\\kz\\greetgo\\sandbox\\db\\migration\\migration_conf.txt"));
    String[] params = new String[3];
    String param;
    int i = 0;
    while ((param = bufferedReader.readLine()) != null) {
      System.out.println(param);
      if (i == 0) {
        params[0] = param;
        i++;
        continue;
      }

      param = param.replace("user:", "");
      param = param.replace("password:", "");
      params[i++] = param;
    }
    return params;
  }

  public static void main(String[] args) throws ParseException {
    System.out.println(new SimpleDateFormat("yyyy-mm-dd").parse("1980-01-01"));
    System.out.println(new File("").getAbsolutePath());
    InMigration inMigration = new InMigration();
    try {
      inMigration.execute();
    } catch (Exception e) {

    }
  }

  public void close() throws SQLException {
    connection.close();
  }
}
