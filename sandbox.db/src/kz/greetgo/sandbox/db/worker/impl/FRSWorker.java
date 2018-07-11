package kz.greetgo.sandbox.db.worker.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import kz.greetgo.sandbox.controller.model.TMPClientAccount;
import kz.greetgo.sandbox.controller.model.TMPClientAccountTransaction;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.sql.Connection;

public class FRSWorker extends Worker {

  private static Logger logger = Logger.getLogger(FRSWorker.class);

  private JsonParser jsonParser;

  private File clientAccountCsvFile;
  private File clientAccountTransactionCsvFile;
  private File transactionTypeCsvFile;

  private Writer clientAccountCsvBw;
  private Writer clientAccountTransactionCsvBw;
  private Writer transactionTypeCsvBw;

  private String clientAccountTmp;
  private String clientAccountTransactionTmp;
  private String transactionTypeTmp;

  public FRSWorker(Connection connection, InputStream inputStream) {
    super(connection, inputStream);
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
    logger.info("create tmp tables begin...");
    setTmpTableNames();
    exec("CREATE TABLE TMP_TABLE(client_cia_id VARCHAR(255), registered_at VARCHAR(255), account_number VARCHAR(255))", clientAccountTmp);
    exec("CREATE TABLE TMP_TABLE(money FLOAT, finished_at VARCHAR(255), transaction_type VARCHAR(255), account_number VARCHAR(255))", clientAccountTransactionTmp);
    exec("CREATE TABLE TMP_TABLE(name VARCHAR(255));", transactionTypeTmp);
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
    clientAccountCsvFile = createFile("client_account_csv_file.csv");
    clientAccountTransactionCsvFile = createFile("client_account_transaction_csv_file.csv");
    transactionTypeCsvFile = createFile("transaction_type_csv_file.csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    clientAccountCsvBw = getWriter(clientAccountCsvFile);
    clientAccountTransactionCsvBw = getWriter(clientAccountTransactionCsvFile);
    transactionTypeCsvBw = getWriter(transactionTypeCsvFile);
  }

  @Override
  public void loadCsvFile() {
    logger.info("load csv files begin...");
    JSONHandler handler = new JSONHandler();
    try {
      handler.startDocument();
      while (jsonParser.nextToken() != null) {
        handler.element(jsonParser.readValueAsTree());
      }
      handler.endDocument();
    } catch (Exception e) {
      logger.error(e);
    }
    logger.info("load csv files end.");
  }

  private void write(TMPClientAccount tmp) {
    try {
      clientAccountCsvBw.write(String.format("%s|%s|%s\n",
        checkStr(tmp.clientId,null), checkStr(tmp.registeredAt,System.nanoTime()),
        checkStr(tmp.accountNumber,System.nanoTime())));
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private void write(TMPClientAccountTransaction tmp) {
    try {
      clientAccountTransactionCsvBw.write(String.format("%s|%s|%s|%s\n",
        checkStr(tmp.money,null).replace("_", ""), checkStr(tmp.finishedAt,null),
        checkStr(tmp.transactionType,System.nanoTime()), checkStr(tmp.accountNumber,null)));
      transactionTypeCsvBw.write(String.format("%s\n", checkStr(tmp.transactionType, null)));
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void loadCsvFilesToTmpTables() {
    logger.info("load csv files to tmp begin...");
    CopyManager copyManager;
    try {
      flushWriters();

      copyManager = new CopyManager((BaseConnection) connection);

      copy(copyManager, clientAccountCsvFile, clientAccountTmp);
      copy(copyManager, clientAccountTransactionCsvFile, clientAccountTransactionTmp);
      copy(copyManager, transactionTypeCsvFile, transactionTypeTmp);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    logger.info("load csv files to tmp end...");
  }

  @Override
  public void fuseMainTmpTables() {
    logger.info("fuse main tmp tables begin...");
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", transactionTypeTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE(name VARCHAR(255) UNIQUE NOT NULL);", transactionTypeTmp);
    //language=PostgreSQL
    exec("INSERT INTO TMP_TABLE SELECT DISTINCT \"name\" " +
      "FROM TMP_TABLE_conductor t1 " +
      "WHERE t1.name IS NOT NULL " +
      "ON CONFLICT(\"name\") " +
      "DO NOTHING;", transactionTypeTmp);

    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor;", clientAccountTmp);
    //language=PostgreSQL
    exec("SELECT (SELECT id FROM client WHERE cia_id=t1.client_cia_id) AS client_id," +
      "  CAST(NULL AS VARCHAR(255)) AS error," +
      "  CAST(0 AS FLOAT) AS money," +
      "  split_part(max(registered_at), '#', 2) AS registered_at," +
      "  split_part(max(account_number), '#', 2) AS account_number " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor t1 GROUP BY client_id;", clientAccountTmp);

    //language=PostgreSQL
    exec("UPDATE TMP_TABLE t1 SET money=t1.money+t2.money FROM "+clientAccountTransactionTmp+" t2 WHERE t1.account_number=t2.account_number", clientAccountTmp);
    logger.info("duse main tmp tables end.");
  }

  @Override
  public void validateMainTmpTables() {
    logger.info("valid main tmp tables begin...");
    exec("UPDATE TMP_TABLE SET error='Клиент не найден' " +
      "WHERE error IS NULL AND client_id IS NULL;", clientAccountTmp);
    logger.info("valid main tmp tables end.");
  }

  @Override
  public void migrateMainTmpTableToTables() {
    logger.info("migrate main tmp tables begin...");
    //language=PostgreSQL
    exec("INSERT INTO client_account(client, money, registered_at, number) " +
      "SELECT client_id, money, registered_at::DATE, account_number FROM TMP_TABLE WHERE error IS NULL " +
      "ON CONFLICT(number) DO UPDATE SET " +
      "money=EXCLUDED.money," +
      "registered_at=EXCLUDED.registered_at;", clientAccountTmp);
    //language=PostgreSQL
    exec("INSERT INTO transaction_type(name)" +
      "SELECT \"name\" FROM TMP_TABLE ON CONFLICT(name) DO NOTHING;", transactionTypeTmp);
    logger.info("migrate main tmp tables end.");
  }

  @Override
  public void fuseChildTmpTables() {
    logger.info("fuse child tmp tables begin...");
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientAccountTransactionTmp);
    //language=PostgreSQL
    exec("SELECT " +
      "(SELECT id FROM client_account WHERE client_account.number=t1.account_number) AS client_account_id, " +
      "CAST(NULL AS VARCHAR(255)) AS error, " +
      "money, " +
      "(SELECT id FROM transaction_type WHERE \"name\"=split_part(max(transaction_type), '#', 2)) AS transaction_type, " +
      "finished_at, account_number " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor t1 " +
      "GROUP BY money, finished_at, account_number;", clientAccountTransactionTmp);
    logger.info("fuse child tmp tables end.");
  }

  @Override
  public void validateChildTmpTables() {
    logger.info("valid child tmp tables begin...");
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='Клиент не найден' " +
      "WHERE error IS NULL AND client_account_id IS NULL;", clientAccountTransactionTmp);
    logger.info("valid child tmp tables end.");
  }

  @Override
  public void migrateChildTmpTablesToTables() {
    logger.info("migrate child tmp tables begin...");
    //language=PostgreSQL
    exec("INSERT INTO client_account_transaction(account, money, finished_at, type) " +
      "SELECT client_account_id, money::FLOAT, finished_at::DATE," +
      "transaction_type " +
      "FROM TMP_TABLE WHERE error IS NULL;", clientAccountTransactionTmp);
    logger.info("migrate child tmp tables end.");
  }

  private void flushWriters() throws IOException {
    clientAccountCsvBw.flush();
    clientAccountTransactionCsvBw.flush();
    transactionTypeCsvBw.flush();
  }

  @Override
  public void deleteTmpTables() {
    logger.info("delete tmp tables begin...");
    deleteTable(clientAccountTmp);
    deleteTable(clientAccountTransactionTmp);
    deleteTable(transactionTypeTmp);
    logger.info("delete tmp tables end.");
  }

  @Override
  public void finish() throws IOException {
    logger.info("finish method begin...");
    clientAccountCsvBw.close();
    clientAccountTransactionCsvBw.close();
    transactionTypeCsvBw.close();

    clientAccountCsvFile.delete();
    clientAccountTransactionCsvFile.delete();
    transactionTypeCsvFile.delete();

    jsonParser.close();
    logger.info("finish method end.");
  }

  private class JSONHandler extends DefaultHandler {

    private TMPClientAccount tmpClientAccount = new TMPClientAccount();
    private TMPClientAccountTransaction tmpClientTransaction = new TMPClientAccountTransaction();

    @Override
    public void startDocument() {
      logger.info("handler start document...");
    }

    void element(JsonNode jsonNode) {
      JsonNode type = jsonNode.get("type");
      if (type == null) return;

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
      String text = res.asText();
      if (text.equals("null")) return null;
      return res.asText();
    }

    @Override
    public void endDocument() {
      logger.info("handler end document.");
    }
  }
}
