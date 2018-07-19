package kz.greetgo.sandbox.db.migration.reader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.migration.reader.AccountSenderThread;
import kz.greetgo.sandbox.db.migration.reader.TransactionSenderThread;
import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.AccountProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.TransactionProcessor;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class JSONManager {

  private String filePath;

  private List<TransactionSenderThread> transactionSenderThreads = new LinkedList<>();
  private List<AccountSenderThread> accountSenderThreads = new LinkedList<>();

  public JSONManager(String filePath) {
    this.filePath = filePath;
  }

  private int accountSenderThreadNum = 0;
  private int transactionSenderThreadNum = 0;


  private List<NewAccountFromMigration> accounts;
  private List<TransactionFromMigration> transactions;

  public void load(TransactionProcessor transactionProcessor, AccountProcessor accountProcessor) throws IOException, InterruptedException {
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String current;

    transactions = new LinkedList<>();
    accounts = new LinkedList<>();

    while ((current = reader.readLine()) != null) {
      ObjectMapper mapper = new ObjectMapper();

      joinDeadThreads();

      if (current.contains("new_account")) {
        NewAccountFromMigration mapped = mapper.readValue(current, NewAccountFromMigration.class);
        accounts.add(mapped);
      } else {
        TransactionFromMigration mapped = mapper.readValue(current, TransactionFromMigration.class);
        transactions.add(mapped);
      }

      if (transactions.size() >= 1000) {
        sendTransactions(transactionProcessor);
      }
      if (accounts.size() >= 1000) {
        sendAccounts(accountProcessor);
      }
    }
    reader.close();
    sendTransactions(transactionProcessor);
    sendAccounts(accountProcessor);
    joinDeadThreads();
  }

  private void sendTransactions(TransactionProcessor transactionProcessor) {
    transactionSenderThreadNum++;
    List<TransactionFromMigration> transactionsFromMigration = new LinkedList<>(transactions);
    transactionSenderThreads.add(new TransactionSenderThread(transactionProcessor, transactionsFromMigration));
    transactionSenderThreads.get(transactionSenderThreads.size() - 1).start();
    transactions = new LinkedList<>();
  }

  private void sendAccounts(AccountProcessor accountProcessor) {
    accountSenderThreadNum++;
    List<NewAccountFromMigration> accountsFromMigration = new LinkedList<>(accounts);
    accountSenderThreads.add(new AccountSenderThread(accountProcessor, accountsFromMigration));
    accountSenderThreads.get(accountSenderThreads.size() - 1).start();
    accounts = new LinkedList<>();
  }


  private void joinDeadThreads() throws InterruptedException {
    while (accountSenderThreadNum > 1 && transactionSenderThreadNum > 1) {
      for (TransactionSenderThread thread : transactionSenderThreads) {
        if (!thread.isAlive()) {
          thread.join();
          transactionSenderThreads.remove(thread);
          transactionSenderThreadNum--;
          break;
        }
      }

      for (AccountSenderThread thread : accountSenderThreads) {
        if (!thread.isAlive()) {
          thread.join();
          accountSenderThreads.remove(thread);
          accountSenderThreadNum--;
          break;
        }
      }
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException, SQLException {
    long time = System.nanoTime();
    JSONManager manager = new JSONManager("C:\\Programs\\Web\\Greetgo\\from_frs_10000007.txt");
    System.out.println("LOADING");
    Connection connection;
    Properties properties = new Properties();
    properties.setProperty("user", "kayne_sandbox");
    properties.setProperty("password", "111");
    connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kayne_sandbox", properties);
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setAutoCommit(false);


    FRSInMigration frsInMigration = new FRSInMigration(connection);
    frsInMigration.prepareWorker();
    frsInMigration.createTempTables();

    manager.load(transactions -> {
      frsInMigration.sendTransactions(transactions);
    }, accounts -> {
      frsInMigration.sendAccounts(accounts);
    });

    long endTime = System.nanoTime();
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println("Elapsed time: " + ((endTime - time) / 1000000000.0));
  }
}
