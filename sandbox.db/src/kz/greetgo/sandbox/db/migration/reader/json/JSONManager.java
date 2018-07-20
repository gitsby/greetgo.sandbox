package kz.greetgo.sandbox.db.migration.reader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.migration.reader.objects.TempAccount;
import kz.greetgo.sandbox.db.migration.reader.objects.TempTransaction;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigrationWorker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class JSONManager {

  private String filePath;

  private int accountBatch = 0;
  private int transactionBatch = 0;

  public JSONManager(String filePath) {
    this.filePath = filePath;
  }


  public void load(Connection connection, PreparedStatement accountStatement, PreparedStatement transactionStatement) throws IOException, ParseException, SQLException {
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String current;

    while ((current = reader.readLine()) != null) {
      ObjectMapper mapper = new ObjectMapper();

      if (current.contains("new_account")) {
        TempAccount account = mapper.readValue(current, TempAccount.class);
        batchInsert(accountStatement, account.account_number, account.client_id, timeStampFromString(account.registered_at.replace("T", " ")));
        accountBatch++;
      } else {
        TempTransaction transaction = mapper.readValue(current, TempTransaction.class);
        batchInsert(transactionStatement, transaction.account_number,
          Float.valueOf(transaction.money.replace("_", ""))
          , transaction.transaction_type
          , timeStampFromString(transaction.finished_at.replace("T", " ")));
        transactionBatch++;
      }

      if (accountBatch > 10000) {
        accountBatch = 0;
        accountStatement.executeBatch();
        connection.commit();
      }
      if (transactionBatch > 10000) {
        transactionBatch = 0;
        transactionStatement.executeBatch();
        connection.commit();
      }

    }
    accountStatement.executeBatch();
    transactionStatement.executeBatch();
    connection.commit();
    reader.close();
  }

  private void batchInsert(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      statement.setObject(i + 1, params[i]);
    }
    statement.addBatch();
  }

  private Timestamp timeStampFromString(String date) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd hh:mm:ss.SSS");

    Date parsedTimeStamp = dateFormat.parse(date);

    Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());

    return timestamp;
  }

}
