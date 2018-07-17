package kz.greetgo.sandbox.db.migration.workers.frs;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.workers.DBConnector;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class FRSInMigration extends DBConnector {

  FRSInMigrationWorker frs;

  public void prepareWorker() {
    frs = new FRSInMigrationWorker(connection);
  }

  public void createTempTables() throws SQLException {
    frs.prepare();
  }

  public void insertTempAccounts() throws SQLException {
    frs.insertIntoAccount();
  }

  public void insertTempTransactions() throws SQLException {
    frs.insertIntoTransaction();
  }

  public void sendTransactions(List<TransactionFromMigration> transactions) throws SQLException, ParseException {
    frs.sendTransactions(transactions);
  }

  public void sendAccounts(List<NewAccountFromMigration> accounts) throws SQLException, ParseException {
    frs.sendAccounts(accounts);
  }

  public void dropTempTables() throws SQLException {
    frs.dropTempTables();
  }
}
