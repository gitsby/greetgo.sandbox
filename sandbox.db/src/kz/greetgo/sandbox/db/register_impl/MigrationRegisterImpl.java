package kz.greetgo.sandbox.db.register_impl;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.migration.archiver.ArchiveUtils;
import kz.greetgo.sandbox.db.migration.connection.SSHConnector;
import kz.greetgo.sandbox.db.migration.reader.json.JSONManager;
import kz.greetgo.sandbox.db.migration.reader.xml.XMLManager;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigration;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class MigrationRegisterImpl implements MigrationRegister {

  CIAInMigration cia = new CIAInMigration();

  FRSInMigration frs = new FRSInMigration();

  SSHConnector connector;

  List<String> files;

  @Override
  public void migrate() throws Exception {
    long startTime = System.nanoTime();
    connectSSH();
    prepareCIAFRS();

    downloadFiles();
    unpackFiles();

    insertCIAIntoTemp();
    insertFRSIntoTemp();

    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;

    System.out.println("ENDED IN:" + totalTime / 1000000000.0);

    System.out.println("INSERTING FROM TEMP");

    Thread.sleep(2000);

    insertCIAIntoReal();
    insertFRSIntoReal();

    System.out.println("------------FINISHED ALL TASKS------------------------");
    dropTempTables();
  }

  private void dropTempTables() throws SQLException {
    cia.dropTempTables();
    cia.closeConnection();

    frs.dropTempTables();
    frs.closeConnection();

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
    connector = SSHConnector.getConnection();
    connector.openConnection();
  }

  private void prepareCIAFRS() throws Exception {
    cia.connect();
    cia.prepareWorker();
    cia.createTempTables();

    frs.connect();
    frs.prepareWorker();
    frs.createTempTables();
  }

  public void downloadFiles() throws JSchException, SftpException, IOException {
    connector.sendCommand("cd test/; ls");
    files = connector.recData();
    for (String file : files) {
      connector.downloadFile(file);
    }

  }

  public void insertCIAIntoTemp() {
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

  public void unpackFiles() throws IOException {
    for (String file : files) {
      ArchiveUtils.extract(file);
    }
  }

  public void insertFRSIntoTemp() throws IOException, InterruptedException {
    for (String file : files) {
      if (file.contains(".txt")) {
        String txtFile = file.replace(".tar.bz2", "");

        JSONManager manager = new JSONManager("build/" + txtFile + "/build/out_files/" + txtFile);
        manager.load(transactions -> frs.sendTransactions(transactions),
          accounts -> frs.sendAccounts(accounts));
      }
    }
  }

  private void closeSSH() {
    connector.close();
  }

  public static void main(String[] args) throws Exception {
    MigrationRegisterImpl migrationRegister = new MigrationRegisterImpl();
    long startTime = System.nanoTime();
    migrationRegister.migrate();
    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;
    System.out.println(totalTime / 1000000000.0);
    migrationRegister.closeSSH();
  }

}
