package kz.greetgo.sandbox.db.worker.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import kz.greetgo.sandbox.controller.model.MigrationError;
import kz.greetgo.sandbox.controller.model.TMPClientAccount;
import kz.greetgo.sandbox.controller.model.TMPClientAccountTransaction;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class FRSWorker extends Worker {

  private static Logger logger = Logger.getLogger("migration");

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

  private long clientAccountCount = 0;
  private long clientAccountTransactionCount = 0;

  private long startedAt = System.nanoTime();

  private final AtomicBoolean working = new AtomicBoolean(true);
  private final AtomicBoolean showStatus = new AtomicBoolean(false);

  public FRSWorker(Connection connection, InputStream inputStream) {
    super(connection, inputStream);
    initParser();
  }

  @Override
  public synchronized void fillTmpTables() {
    createTmpTables();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmpTables();
  }

  @Override
  public void margeTmpTables() {
     margeTransactionTypeTmpTable();
     margeClientAccountTmpTable();
     margeClientAccountTransactionTmpTable();
  }

  @Override
  public void validTmpTables() {
    logger.info("Validate of tmp tables begin...");
    validClientAccountTmpTable();
    validClientAccountTransactionTmpTable();
    logger.info("Validate of tmp tables end.");
  }

  @Override
  public void migrateTmpTables() {
    migrateClientAccountTable();
    migrateClientAccountTransactionTable();
  }

  private void initParser() {
    try {
      jsonParser = new MappingJsonFactory().createParser(inputStream);
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void createTmpTables() {
    logger.info("Create of tmp tables begin...");
    setTmpTableNames();
    exec("CREATE TABLE TMP_TABLE(client_cia_id VARCHAR(255), error VARCHAR(255), money FLOAT, registered_at VARCHAR(255), account_number VARCHAR(255))", clientAccountTmp);
    exec("CREATE TABLE TMP_TABLE(money FLOAT, error VARCHAR(255), finished_at DATE, transaction_type VARCHAR(255), account_number VARCHAR(255))", clientAccountTransactionTmp);
    exec("CREATE TABLE TMP_TABLE(name VARCHAR(255));", transactionTypeTmp);
    logger.info("Create of tmp tables end");
  }

  private void setTmpTableNames() {
    setTmpTableNames(getNameWithDate("frs_migration_client_account_tmp"),
      getNameWithDate("frs_migration_client_account_transaction_tmp"),
      getNameWithDate("frs_migration_transaction_type_tmp"));
  }

  public void setTmpTableNames(String clientAccountTmp, String clientAccountTransactionTmp, String transactionTypeTmp) {
    this.clientAccountTmp = clientAccountTmp;
    this.clientAccountTransactionTmp = clientAccountTransactionTmp;
    this.transactionTypeTmp = transactionTypeTmp;
  }

  private void createCsvFiles() {
    logger.info("Create of csv files begin");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("Create of csv files end");
  }

  private void createFiles() throws IOException {
    clientAccountCsvFile = createFile(getNameWithDate("client_account_csv_file")+".csv");
    clientAccountTransactionCsvFile = createFile(getNameWithDate("client_account_transaction_csv_file")+".csv");
    transactionTypeCsvFile = createFile(getNameWithDate("transaction_type_csv_file")+".csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    clientAccountCsvBw = getWriter(clientAccountCsvFile);
    clientAccountTransactionCsvBw = getWriter(clientAccountTransactionCsvFile);
    transactionTypeCsvBw = getWriter(transactionTypeCsvFile);
  }

  private void loadCsvFile() {
    logger.info("Load of csv files begin...");
    try {
      loadCsvFileInner();
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("Load of csv files end.");
  }

  private void loadCsvFileInner() throws IOException {
    JSONHandler handler = new JSONHandler();

    final Thread see = getTimer(working, showStatus);
    see.start();

    handler.startDocument();
    while (jsonParser.nextToken() != null) {
      checkShowStatus();
      handler.element(jsonParser.readValueAsTree());
    }
    handler.endDocument();
  }

  private void checkShowStatus() {
    if (showStatus.get()) {
      showStatus.set(false);
      long now = System.nanoTime();
      logger.info(" -- downloaded client_account " + clientAccountCount + " for " + showTime(now, startedAt)
        + " : " + recordsPerSecond(clientAccountCount, now - startedAt));
      logger.info(" -- downloaded client_account_transaction " + clientAccountTransactionCount + " for " + showTime(now, startedAt)
        + " : " + recordsPerSecond(clientAccountTransactionCount, now - startedAt));
    }
  }

  private void write(TMPClientAccount tmp) {
    try {
      clientAccountCount++;
      clientAccountCsvBw.write(String.format("%s|\\N|0|%s|%s\n",
        checkIsNull(tmp.clientId), checkIsNull(tmp.registeredAt),
        checkIsNull(tmp.accountNumber)));
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void write(TMPClientAccountTransaction tmp) {
    try {
      clientAccountTransactionCount++;
      clientAccountTransactionCsvBw.write(String.format("%s|\\N|%s|%s|%s\n",
        checkIsNull(tmp.money).replace("_", ""), checkIsNull(tmp.finishedAt),
        checkIsNull(tmp.transactionType), checkIsNull(tmp.accountNumber)));
      transactionTypeCsvBw.write(String.format("%s\n", checkIsNull(tmp.transactionType)));
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void loadCsvFilesToTmpTables() {
    logger.info("Load of csv files to tmp begin...");
    working.set(false);
    try {
      flushWriters();
      copy(getCopyManager(), clientAccountCsvFile, clientAccountTmp);
      copy(getCopyManager(), clientAccountTransactionCsvFile, clientAccountTransactionTmp);
      copy(getCopyManager(), transactionTypeCsvFile, transactionTypeTmp);
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("Load of csv files to tmp end...");
  }

  private void flushWriters() throws IOException {
    clientAccountCsvBw.flush();
    clientAccountTransactionCsvBw.flush();
    transactionTypeCsvBw.flush();
  }


  private void margeTransactionTypeTmpTable() {
    if (transactionTypeTmp != null) {
      exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", transactionTypeTmp);
      exec("SELECT DISTINCT \"name\" INTO TMP_TABLE FROM conductor_TMP_TABLE", transactionTypeTmp);
      exec("DROP TABLE conductor_TMP_TABLE", transactionTypeTmp);
    }
  }

  private void margeClientAccountTmpTable() {
    logger.info("Marge client accounts in tmp tables begin...");
    exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE;", clientAccountTmp);
    exec("SELECT DISTINCT " +
      "  (SELECT id FROM client WHERE cia_id=client_cia_id LIMIT 1) AS client_id," +
      "  client_cia_id, account_number, " +
      "  error, " +
      "  CAST(0 AS FLOAT) AS money, " +
      "  registered_at " +
      "INTO TMP_TABLE " +
      "FROM conductor_TMP_TABLE t1 WHERE error IS NULL;", clientAccountTmp);
    exec("DROP TABLE conductor_TMP_TABLE", clientAccountTmp);
    logger.info("Marge of client accounts in tmp tables end.");
  }

  private void margeClientAccountTransactionTmpTable() {
    logger.info("Marge of client account transactions in tmp tables begin...");
    exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", clientAccountTransactionTmp);
    exec(
      "SELECT DISTINCT " +
        "  CAST(0 AS INTEGER) AS account_id," +
        "  account_number, " +
        "  error, " +
        "  money, " +
        "  finished_at, " +
        "  FIRST_VALUE(transaction_type) OVER " +
        "    ( PARTITION BY account_number, money, finished_at " +
        "    ORDER BY CASE WHEN transaction_type IS NULL " +
        "      THEN NULL " +
        "             ELSE timeofday() " +
        "             END DESC NULLS LAST " +
        "    ) AS transaction_type " +
        "INTO TMP_TABLE " +
        "FROM conductor_TMP_TABLE t1;", clientAccountTransactionTmp);
    exec("DROP TABLE conductor_TMP_TABLE", clientAccountTransactionTmp);
    logger.info("Marge of client account transactions in tmp tables end.");
  }

  private void validClientAccountTmpTable() {
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND client_cia_id IS NULL;", clientAccountTmp);
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND account_number IS NULL;", clientAccountTmp);
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.CLIENT_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND client_id IS NULL;", clientAccountTmp);
  }

  private void validClientAccountTransactionTmpTable() {
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND account_number IS NULL;", clientAccountTransactionTmp);
  }

  private void migrateClientAccountTable() {
    logger.info("Migrate of client account from tmp tables to real begin...");
    exec("UPDATE TMP_TABLE t1 SET money=t1.money+t2.money FROM "+clientAccountTransactionTmp+" t2 WHERE t1.account_number=t2.account_number AND t1.error IS NULL", clientAccountTmp);
    exec("INSERT INTO client_account(client, money, registered_at, number) " +
      "SELECT client_id, money, registered_at::DATE, account_number FROM TMP_TABLE WHERE error IS NULL " +
      "ON CONFLICT(number) DO UPDATE SET " +
      "money=EXCLUDED.money," +
      "registered_at=EXCLUDED.registered_at;", clientAccountTmp);
    exec("INSERT INTO transaction_type(name)" +
      "SELECT \"name\" FROM TMP_TABLE ON CONFLICT(name) DO NOTHING;", transactionTypeTmp);
    logger.info("Migrate of client account from tmp tables to real end.");
  }

  private void migrateClientAccountTransactionTable() {
    logger.info("Migrate of client account transaction from tmp tables to real begin...");
    exec("UPDATE TMP_TABLE SET account_id=(SELECT id FROM client_account WHERE number=account_number) WHERE error IS NULL", clientAccountTransactionTmp);
    exec("UPDATE TMP_TABLE SET error='client_account_not_found' WHERE error IS NULL AND account_id IS NULL", clientAccountTransactionTmp);
    exec("INSERT INTO client_account_transaction(account, money, finished_at, type) " +
      "SELECT account_id, money, finished_at," +
      "(SELECT id FROM transaction_type WHERE name=transaction_type) " +
      "FROM TMP_TABLE WHERE error IS NULL;", clientAccountTransactionTmp);
    logger.info("Migrate of client account transaction from tmp tables to real end.");
  }

  @Override
  public void deleteTmpTables() {
    logger.info("Drop of tmp tables begin...");
    dropTmpTable(clientAccountTmp);
    dropTmpTable(clientAccountTransactionTmp);
    dropTmpTable(transactionTypeTmp);
    logger.info("Drop of tmp tables end.");
  }

  @Override
  public File writeOutErrorData() {
    logger.info("Copy errors from client_accounts tmp table to file begin...");
    File errors = getFile(getNameWithDate("migrated_frs_errors")+".csv");
    try (Writer writer = getWriter(errors)){
      copyOut(getCopyManager(), clientAccountTmp, writer);
      copyOut(getCopyManager(), clientAccountTransactionTmp, writer);
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("Copy errors from client_accounts tmp table to file end.");
    return errors;
  }

  @Override
  public void close() throws IOException {
    logger.info("Close method begin...");

    closeWriter(clientAccountCsvBw);
    closeWriter(clientAccountTransactionCsvBw);
    closeWriter(transactionTypeCsvBw);

    deleteFile(clientAccountCsvFile);
    deleteFile(clientAccountTransactionCsvFile);
    deleteFile(transactionTypeCsvFile);

    jsonParser.close();
    try {
      connection.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    logger.info("Close method end.");
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
