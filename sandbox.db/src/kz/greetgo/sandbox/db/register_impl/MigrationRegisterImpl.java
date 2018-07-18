package kz.greetgo.sandbox.db.register_impl;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.SSHConfig;
import kz.greetgo.sandbox.db.migration.archiver.ArchiveUtils;
import kz.greetgo.sandbox.db.migration.connection.SSHConnector;
import kz.greetgo.sandbox.db.migration.reader.json.JSONManager;
import kz.greetgo.sandbox.db.migration.reader.xml.XMLManager;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigration;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<SSHConfig> sshConfig;
  public BeanGetter<DbConfig> dbConfig;

  CIAInMigration cia;
  FRSInMigration frs;

  Connection connection;
  SSHConnector connector;

  List<String> files;

  @Override
  public void migrate() throws Exception {
    connectToDatabase();

    cia = new CIAInMigration(connection);
    frs = new FRSInMigration(connection);

    connectSSH();
    prepareCIAFRS();

    downloadFiles();
    unpackFiles();

    Thread ciaTempThread = new Thread(this::insertCIAIntoTemp);
    Thread frsThread = new Thread(() -> {
      try {
        insertFRSIntoTemp();
      } catch (Exception e) {
      }
    });

    ciaTempThread.start();
    frsThread.start();

    while (ciaTempThread.isAlive() || frsThread.isAlive()) ;

    updateError();

    insertCIAIntoReal();
    insertFRSIntoReal();

    dropTempTables();
    closeSSH();
  }

  private void updateError() throws SQLException, IOException, SftpException, JSchException {
    cia.updateError();
    connector.uploadErrorFile();
  }

  private void dropTempTables() throws SQLException {
    cia.dropTempTables();
    cia.closeConnection();

    frs.dropTempTables();
    cia.closeConnection();
  }


  private void insertFRSIntoReal() throws SQLException {
    frs.insertTempAccounts();
    frs.insertTempTransactions();
  }

  private void insertCIAIntoReal() throws SQLException {
    cia.insertTempClientsToReal();
    cia.insertTempAddressToReal();
    cia.insertTempPhone();
  }

  private void connectSSH() throws Exception {
    connector = new SSHConnector(sshConfig.get().ip(), sshConfig.get().port(), sshConfig.get().user(), sshConfig.get().password(), sshConfig.get().timeOut());

    connector.openConnection();
  }

  private void prepareCIAFRS() throws Exception {
    cia.prepareWorker();
    cia.createTempTables();

    frs.prepareWorker();
    frs.createTempTables();
  }

  private void downloadFiles() throws JSchException, SftpException, IOException {
    connector.sendCommand("cd test/; ls");
    files = connector.recData();
    for (String file : files) {
      connector.downloadFile(file);
    }

  }

  private void insertCIAIntoTemp() {
    for (String file : files) {
      if (file.contains(".xml")) {
        String xmlFile = file.replace(".tar.bz2", "");
        XMLManager xmlManager = new XMLManager("build/" + xmlFile + "/build/out_files/" + xmlFile);
        xmlManager.load(cia::sendClient,
          cia::sendAddresses,
          cia::sendPhones
        );
      }
    }
  }

  private void unpackFiles() throws IOException {
    for (String file : files) {
      ArchiveUtils.extract(file);
    }
  }

  private void insertFRSIntoTemp() throws IOException, InterruptedException {
    for (String file : files) {
      if (file.contains(".txt")) {
        String txtFile = file.replace(".tar.bz2", "");

        JSONManager manager = new JSONManager("build/" + txtFile + "/build/out_files/" + txtFile);
        manager.load(transactions -> frs.sendTransactions(transactions),
          accounts -> frs.sendAccounts(accounts));
      }
    }
  }

  private void connectToDatabase() throws SQLException {
    String url = dbConfig.get().url();
    Properties properties = new Properties();
    properties.setProperty("user", dbConfig.get().username());
    properties.setProperty("password", dbConfig.get().password());
    connection = DriverManager.getConnection(url, properties);
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setAutoCommit(false);
  }

  private void closeSSH() {
    connector.close();
  }

}
