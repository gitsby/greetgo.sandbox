package kz.greetgo.sandbox.db.worker.impl;

import com.fasterxml.jackson.databind.JsonNode;
import kz.greetgo.sandbox.controller.model.TMPClientAccount;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.Worker;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FRSWorker extends Worker {

  public FRSWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    super(connections, inputStream, migrationConfig);
  }

  @Override
  public void prepareStatements() throws SQLException {

  }

  @Override
  public void createTmpTables() {

  }

  @Override
  public void createCsvFiles() {

  }

  @Override
  public void loadCsvFile() {

  }

  @Override
  public void loadCsvFilesToTmp() {

  }

  @Override
  public void fuseTmpTables() {

  }

  @Override
  public void validateTmpTables() {

  }

  @Override
  public void migrateToTables() {

  }

  @Override
  public void deleteTmpTables() {

  }

  @Override
  public void finish() throws SQLException {

  }

  class JSONHandler extends DefaultHandler {

    TMPClientAccount tmpClientAccount = new TMPClientAccount();

    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
    }

    public void getElement(JsonNode jsonNode) {
      tmpClientAccount.money = getElement(jsonNode,"money");
      tmpClientAccount.clientId =getElement(jsonNode, "client_id");
      tmpClientAccount.registeredAt = getElement(jsonNode,"registered_at");
      tmpClientAccount.number = getElement(jsonNode,"account_number");
      System.out.println(tmpClientAccount.toString());
    }

    private String getElement(JsonNode node, String element) {
      if (node.get(element) == null) return "";
      return node.get(element).textValue();
    }

    @Override
    public void endDocument() throws SAXException {
      super.endDocument();
    }
  }
}
