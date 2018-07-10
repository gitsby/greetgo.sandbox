package kz.greetgo.sandbox.db.worker.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import kz.greetgo.sandbox.controller.model.TMPClientAccount;
import kz.greetgo.sandbox.controller.model.TMPClientAccountTransaction;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.sql.Connection;
import java.util.List;

public class FRSWorker extends Worker {

  private static Logger logger = Logger.getLogger(FRSWorker.class);
  private JsonParser jsonParser;
  private JSONHandler handler;

  private File clientAccountCsvFile;
  private File clientAccountTransactionCsvFile;
  private File transactionTypeCsvFile;

  private Writer clientAccountCsvBw;
  private Writer clientAccountTransactionCsvBw;
  private Writer transactionTypeCsvBw;

  private String clientAccountTmp;
  private String clientAccountTransactionTmp;
  private String transactionTypeTmp;

  public FRSWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    super(connections, inputStream, migrationConfig);
    initParser();
  }

  private void initParser() {
    try {
      jsonParser = new MappingJsonFactory().createParser(inputStream);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void createTmpTables() {
    logger.info("create tmp tables begin");
    setTmpTableNames();
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE(client_id VARCHAR(255), error VARCHAR(255), registered_at VARCHAR(255), account_number VARCHAR(255))", clientAccountTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE(money VARCHAR(255), error VARCHAR(255), finished_at VARCHAR(255), transaction_type VARCHAR(255), account_number VARCHAR(255))", clientAccountTransactionTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE()", transactionTypeTmp);
    logger.info("create tmp tables end");
  }

  private void setTmpTableNames() {
    clientAccountTmp = getTmpTableName("client_account_tmp");
    clientAccountTransactionTmp = getTmpTableName("client_account_transaction_tmp");
    transactionTypeTmp = getTmpTableName("transaction_type_tmp");
  }

  @Override
  public void createCsvFiles() {
    logger.info("create csv files begin");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    logger.info("create csv files end");
  }

  private void createFiles() throws IOException {
    clientAccountCsvFile = createFile(TMP_DIR+"client_account_csv_file.csv");
    clientAccountTransactionCsvFile = createFile(TMP_DIR+"client_account_transaction_csv_file.csv");
    transactionTypeCsvFile = createFile(TMP_DIR+"transaction_type_csv_file.csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    clientAccountCsvBw = getWriter(clientAccountCsvFile);
    clientAccountTransactionCsvBw = getWriter(clientAccountTransactionCsvFile);
    transactionTypeCsvBw = getWriter(transactionTypeCsvFile);
  }

  @Override
  public void loadCsvFile() {
    handler = new JSONHandler();
    try {
      handler.startDocument();
      while (jsonParser.nextToken() != null) {
        handler.element(jsonParser.readValueAsTree());
      }
      handler.endDocument();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  private void write(TMPClientAccount tmp) {
    try {
      clientAccountCsvBw.write(String.format("%s|\\N|%s|%s\n",
        checkStr(tmp.clientId,null), checkStr(tmp.registeredAt,null),
        checkStr(tmp.accountNumber,null)));
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private void write(TMPClientAccountTransaction tmp) {
    try {
      clientAccountTransactionCsvBw.write(String.format("%s|\\N|%s|%s|%s\n",
        checkStr(tmp.money,null), checkStr(tmp.finishedAt,null),
        checkStr(tmp.transactionType,null), checkStr(tmp.accountNumber,null)));
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void loadCsvFilesToTmp() {
    CopyManager copyManager;
    try {
      flushWriters();

      copyManager = new CopyManager((BaseConnection) nextConnection());

      copy(copyManager, clientAccountCsvFile, clientAccountTmp);
      copy(copyManager, clientAccountTransactionCsvFile, clientAccountTransactionTmp);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void flushWriters() throws IOException {
    clientAccountCsvBw.flush();
    clientAccountTransactionCsvBw.flush();
    transactionTypeCsvBw.flush();
  }

  @Override
  public void fuseTmpTables() {

  }

  @Override
  public void validateTmpTables() {

  }

  @Override
  public void migrateToTables() {
    //language=PostgreSQL
    exec("INSERT INTO client_account(client, money, registered_at) " +
      "SELECT (SELECT id FROM client WHERE cia_id=TMP_TABLE.client_id) AS client, 0, to_date(registered_at, 'yyyy-MM-dd') " +
      "FROM TMP_TABLE;", clientAccountTmp);
  }

  @Override
  public void deleteTmpTables() {

  }

  @Override
  public void finish() {

  }

  class JSONHandler extends DefaultHandler {

    private TMPClientAccount tmpClientAccount = new TMPClientAccount();
    private TMPClientAccountTransaction tmpClientTransaction = new TMPClientAccountTransaction();

    @Override
    public void startDocument() {
      logger.info("begin reading doc");
    }

    public void element(JsonNode jsonNode) {
      JsonNode type = jsonNode.get("type");
      if (type == null) System.out.println(jsonNode.toString());

      switch (type.textValue()) {
        case "transaction":
          parseToClientTransaction(jsonNode);
          write(tmpClientTransaction);
          break;
        case "new_account":
          parseToClientAccount(jsonNode);
          write(tmpClientAccount);
          break;
      }
      reload();
    }

    private void reload() {
      tmpClientAccount = new TMPClientAccount();
      tmpClientTransaction = new TMPClientAccountTransaction();
    }

    private void parseToClientAccount(JsonNode node) {
      tmpClientAccount.clientId = get(node, "client_id");
      tmpClientAccount.registeredAt = get(node, "registered_at");
      tmpClientAccount.accountNumber = get(node, "account_number");
    }

    private void parseToClientTransaction(JsonNode node) {
      tmpClientTransaction.money = get(node, "money");
      tmpClientTransaction.finishedAt = get(node, "finished_at");
      tmpClientTransaction.transactionType = get(node, "transaction_type");
      tmpClientTransaction.accountNumber = get(node, "account_number");
    }

    public String get(JsonNode node, String key) {
      JsonNode res = node.get(key);
      if (res == null) return null;
      return res.asText();
    }

    @Override
    public void endDocument() {
      logger.info("end reading doc");
    }
  }
}
