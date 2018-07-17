package kz.greetgo.sandbox.db.migration.reader.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.db.migration.reader.AccountSenderThread;
import kz.greetgo.sandbox.db.migration.reader.TransactionSenderThread;
import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.AccountProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.TransactionProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
    String current = null;

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
      System.out.println("THREADS ARE BUSY" + accountSenderThreadNum + " " + transactionSenderThreadNum);
      Thread.sleep(100);

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

  public static void main(String[] args) throws IOException, InterruptedException {
    long time = System.nanoTime();
    JSONManager manager = new JSONManager("C:\\Programs\\Web\\Greetgo\\from_frs_30000.txt");
    System.out.println("LOADING");
    manager.load(transactions -> {
      System.out.println("TRANSACTIONS:" + transactions.size() + "::::");
      System.out.println("MONEY: " + Float.valueOf(transactions.get(0).money.replace("_", "")));
      System.out.println(transactions);
    }, accounts -> {
      System.out.println("ACCOUNTS:" + accounts.size() + "::::");
      System.out.println(accounts + "\n");
    });
    long endTime = System.nanoTime();
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println("Elapsed time: " + ((endTime - time) / 1000000000.0));
  }
}
