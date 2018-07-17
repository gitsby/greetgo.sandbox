package kz.greetgo.sandbox.db.worker.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.MigrationError;
import kz.greetgo.sandbox.controller.model.TMPClientAccount;
import kz.greetgo.sandbox.controller.model.TMPClientAccountTransaction;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.sql.Connection;

@Bean
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

  @Override
  public void fillTmpTables() {
    createTmpTables();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmpTables();
  }

  @Override
  public void margeTmpTables() {
    parallelTasks(
      this::margeTransactionTypeTmpTable,
      this::margeClientAccountTmpTable,
      this::margeClientAccountTransactionTmpTable
    );
  }

  @Override
  public void validTmpTables() {
    logger.info("valid tmp tables begin...");
    parallelTasks(
      this::validClientAccountTmpTable,
      this::validClientAccountTransactionTmpTable
    );
    logger.info("valid tmp tables end.");
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
    logger.info("create tmp tables begin...");
    setTmpTableNames();
    exec("CREATE TABLE TMP_TABLE(client_cia_id VARCHAR(255), error VARCHAR(255), money FLOAT, registered_at VARCHAR(255), account_number VARCHAR(255))", clientAccountTmp);
    exec("CREATE TABLE TMP_TABLE(money FLOAT, error VARCHAR(255), finished_at VARCHAR(255), transaction_type VARCHAR(255), account_number VARCHAR(255))", clientAccountTransactionTmp);
    exec("CREATE TABLE TMP_TABLE(name VARCHAR(255));", transactionTypeTmp);
    logger.info("create tmp tables end");
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
    logger.info("create csv files begin");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("create csv files end");
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
      clientAccountCsvBw.write(String.format("%s|\\N|0|%s|%s\n",
        checkIsNull(tmp.clientId), checkIsNull(tmp.registeredAt),
        checkIsNull(tmp.accountNumber)));
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void write(TMPClientAccountTransaction tmp) {
    try {
      clientAccountTransactionCsvBw.write(String.format("%s|\\N|%s|%s|%s\n",
        checkIsNull(tmp.money).replace("_", ""), checkIsNull(tmp.finishedAt),
        checkIsNull(tmp.transactionType), checkIsNull(tmp.accountNumber)));
      transactionTypeCsvBw.write(String.format("%s\n", checkIsNull(tmp.transactionType)));
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void loadCsvFilesToTmpTables() {
    logger.info("load csv files to tmp begin...");
    CopyManager copyManager;
    try {
      flushWriters();
      copyManager = new CopyManager((BaseConnection) connection);

      parallelTasks(
        () -> copy(copyManager, clientAccountCsvFile, clientAccountTmp),
        () -> copy(copyManager, clientAccountTransactionCsvFile, clientAccountTransactionTmp),
        () -> copy(copyManager, transactionTypeCsvFile, transactionTypeTmp)
      );
    } catch (Exception e) {
      logger.error(e);
    }
    logger.info("load csv files to tmp end...");
  }

  private void flushWriters() throws IOException {
    clientAccountCsvBw.flush();
    clientAccountTransactionCsvBw.flush();
    transactionTypeCsvBw.flush();
  }


  private void margeTransactionTypeTmpTable() {
    if (transactionTypeTmp != null) {
      exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", transactionTypeTmp);
      exec("SELECT DISTINCT \"name\" INTO TMP_TABLE FROM TMP_TABLE_conductor", transactionTypeTmp);
      backgroundTasks(()->exec("DROP TABLE TMP_TABLE_conductor", transactionTypeTmp));
    }
  }

  private void margeClientAccountTmpTable() {
    logger.info("marge client account tmp tables begin...");
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor;", clientAccountTmp);
    exec("SELECT DISTINCT " +
      "  (SELECT id FROM client WHERE cia_id=client_cia_id LIMIT 1) AS client_id," +
      "  client_cia_id, account_number, " +
      "  error, " +
      "  CAST(0 AS FLOAT) AS money, " +
      "  registered_at " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor t1 WHERE error IS NULL;", clientAccountTmp);
    backgroundTasks(()->exec("DROP TABLE TMP_TABLE_conductor", clientAccountTmp));
    logger.info("marge client account tmp tables end.");
  }

  private void margeClientAccountTransactionTmpTable() {
    logger.info("marge client account transaction tmp tables begin...");
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
    backgroundTasks(()->exec("DROP TABLE conductor_TMP_TABLE", clientAccountTransactionTmp));
    logger.info("marge client account transaction tmp tables end.");
  }

  private void validClientAccountTmpTable() {
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND client_cia_id IS NULL;", clientAccountTmp);
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND account_number IS NULL;", clientAccountTmp);
    exec("UPDATE TMP_TABLE SET client_id=0 " +
      "WHERE error IS NULL AND client_id IS NULL;", clientAccountTmp);
  }

  private void validClientAccountTransactionTmpTable() {
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND account_number IS NULL;", clientAccountTransactionTmp);
  }

  private void migrateClientAccountTable() {
    logger.info("migrate client account tmp tables begin...");
    exec("UPDATE TMP_TABLE t1 SET money=t1.money+t2.money FROM "+clientAccountTransactionTmp+" t2 WHERE t1.account_number=t2.account_number AND t1.error IS NULL", clientAccountTmp);
    exec("INSERT INTO client_account(client, money, registered_at, number) " +
      "SELECT client_id, money, registered_at::DATE, account_number FROM TMP_TABLE WHERE error IS NULL " +
      "ON CONFLICT(number) DO UPDATE SET " +
      "money=EXCLUDED.money," +
      "registered_at=EXCLUDED.registered_at;", clientAccountTmp);
    exec("INSERT INTO transaction_type(name)" +
      "SELECT \"name\" FROM TMP_TABLE ON CONFLICT(name) DO NOTHING;", transactionTypeTmp);
    logger.info("migrate client account tmp tables end.");
  }

  private void migrateClientAccountTransactionTable() {
    logger.info("migrate client account transaction tmp tables begin...");
    exec("UPDATE TMP_TABLE SET account_id=(SELECT id FROM client_account WHERE number=account_number)", clientAccountTransactionTmp);
    exec("UPDATE TMP_TABLE SET error='client_account_not_found' WHERE error IS NULL AND account_id IS NULL", clientAccountTransactionTmp);
    exec("INSERT INTO client_account_transaction(account, money, finished_at, type) " +
      "SELECT account_id, money::FLOAT, finished_at::DATE," +
      "(SELECT id FROM transaction_type WHERE name=transaction_type) " +
      "FROM TMP_TABLE WHERE error IS NULL;", clientAccountTransactionTmp);
    logger.info("migrate client account transaction tmp tables end.");
  }

  @Override
  public void deleteTmpTables() {
    logger.info("drop tmp tables begin...");
    parallelTasks(
      () -> exec("DROP TABLE TMP_TABLE", clientAccountTmp),
      () -> exec("DROP TABLE TMP_TABLE", clientAccountTransactionTmp),
      () -> exec("DROP TABLE TMP_TABLE", transactionTypeTmp)
    );
    logger.info("drop tmp tables end.");
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
