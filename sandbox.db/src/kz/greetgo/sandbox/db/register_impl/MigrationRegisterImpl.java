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
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigrationWorker;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigrationWorker;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<SSHConfig> sshConfig;
  public BeanGetter<DbConfig> dbConfig;

  CIAInMigrationWorker cia;
  FRSInMigrationWorker frs;

  Connection connection;
  SSHConnector connector;

  List<String> files;

  public Logger logger = Logger.getLogger(getClass());


  @Override
  public void migrate() throws Exception {
    connectToDatabase();
    logger.info("Connected to database");
    cia = new CIAInMigrationWorker(connection);
    frs = new FRSInMigrationWorker(connection);

    connectSSH();

    logger.info("Connected to SSH Server");
    prepareCIAFRS();

    logger.info("Created temp tables");

    downloadFiles();
    logger.info("Files download complete");


    logger.info("Unpacking started");
    unpackFiles();

    logger.info("Inserting cia to temp started");
    insertCIAIntoTemp();

    logger.info("Inserting frs to temp started");
    insertFRSIntoTemp();

    logger.info("Updating errors");
    updateError();

    logger.info("Inserting cia into real");
    insertCIAIntoReal();

    logger.info("Inserting frs into real");
    insertFRSIntoReal();

    dropTempTables();
    close();
  }

  private void updateError() throws SQLException, IOException, SftpException, JSchException {

    logger.info("Updating cia errors");
    cia.updateError();
    logger.info("Updating cia errors completed");

    logger.info("Updating frs errors");
    frs.updateError();
    logger.info("Updating frs errors completed");

    connector.uploadErrorFile();
  }

  private void dropTempTables() throws Exception {
    cia.dropTempTables();
    cia.close();

    frs.dropTempTables();
    frs.close();
  }


  private void insertFRSIntoReal() throws SQLException {

    logger.info("Inserting temp_accounts into real");
    frs.insertIntoAccount();

    logger.info("Inserting temp_transactions into real");
    frs.insertIntoTransaction();
  }

  private void insertCIAIntoReal() throws SQLException {
    logger.info("Inserting temp_client into real");
    cia.insertIntoClient();

    logger.info("Inserting temp_address into real");
    cia.insertIntoAddress();

    logger.info("Inserting temp_phone into real");
    cia.insertIntoPhone();
  }

  private void connectSSH() throws Exception {
    connector = new SSHConnector(sshConfig.get().ip(), sshConfig.get().port(), sshConfig.get().user(), sshConfig.get().password(), sshConfig.get().timeOut());

    connector.openConnection();
  }

  private void prepareCIAFRS() throws Exception {
    cia.createTempTables();

    frs.createTempTables();
  }

  private void downloadFiles() throws JSchException, SftpException, IOException {
    connector.sendCommand("cd test/; ls");
    files = connector.recData();
    for (String file : files) {
      logger.info("Downloading file:" + file);
      connector.downloadFile(file);
    }

  }

  private void insertCIAIntoTemp() throws IOException, SAXException, ParserConfigurationException {
    for (String file : files) {
      if (file.contains(".xml")) {
        logger.info("Loading CIA file:" + file);
        String xmlFile = file.replace(".tar.bz2", "");
        XMLManager xmlManager = new XMLManager("build/" + xmlFile + "/build/out_files/" + xmlFile);
        xmlManager.load(connection, cia.clientsStatement, cia.phoneStatement, cia.addressStatement);
      }
    }
  }

  private void unpackFiles() throws IOException {
    for (String file : files) {
      logger.info("Unpacking file:" + file);
      ArchiveUtils.extract(file);
    }
  }

  private void insertFRSIntoTemp() throws IOException, ParseException, SQLException {
    for (String file : files) {
      if (file.contains(".txt")) {
        String txtFile = file.replace(".tar.bz2", "");
        logger.info("Loading FRS file:" + file);
        JSONManager manager = new JSONManager("build/" + txtFile + "/build/out_files/" + txtFile);
        manager.load(connection, frs.accountsStatement, frs.transactionStatement);
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

  private void close() throws SQLException {
    connector.close();
    connection.close();
  }

}
