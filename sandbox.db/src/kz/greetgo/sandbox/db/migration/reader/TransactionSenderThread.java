package kz.greetgo.sandbox.db.migration.reader;

import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.TransactionProcessor;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class TransactionSenderThread extends Thread {

  private TransactionProcessor processor;
  private List<TransactionFromMigration> transactions;

  public TransactionSenderThread(TransactionProcessor processor, List<TransactionFromMigration> transactions) {
    this.processor = processor;
    this.transactions = transactions;
  }

  public void run() {
    try {
      processor.sendTransactions(transactions);
    } catch (ParseException e) {

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
