package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.classes.TempAccount;
import kz.greetgo.sandbox.db.classes.TempTransaction;
import kz.greetgo.sandbox.db.migration.reader.json.JSONManager;
import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigration;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientTransactionDot;
import kz.greetgo.sandbox.db.test.dao.FRSMigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class FRSInMigrationTest extends ParentTestNg {

  FRSInMigration frsInMigration = new FRSInMigration();

  public BeanGetter<FRSMigrationTestDao> frsDao;

  JSONManager jsonManager;

  @BeforeMethod
  public void dropTables() throws Exception {
    frsInMigration.connect();
    frsInMigration.prepareWorker();

    frsDao.get().deleteClients();
    frsDao.get().createTempAccountTable();
    frsDao.get().createTempTransactionTable();

  }

  @AfterMethod
  public void createTables() throws SQLException {
    frsDao.get().dropAccountTable();
    frsDao.get().dropTransactionTable();

    frsInMigration.closeConnection();
  }


  @Test
  public void testInsertTransactionIntoTemp() throws IOException, InterruptedException {
    List<TransactionFromMigration> transactions = createTransactions();

    jsonManager = new JSONManager("build/test_frs.txt");
    jsonManager.load(tr -> {
      frsInMigration.sendTransactions(tr);
    }, accounts -> {
    });

    while (frsDao.get().getTempTransactions().size() == 0) ;

    List<TempTransaction> transactionsFromMigration = frsDao.get().getTempTransactions();

    assertThat(transactionsFromMigration).hasSameSizeAs(transactions);

    for (int i = 0; i < transactionsFromMigration.size(); i++) {
      assertThat(transactionsFromMigration.get(i).account_number).isEqualTo(transactions.get(i).account_number);
      assertThat(transactionsFromMigration.get(i).finished_at.toString()).isEqualTo(transactions.get(i).finished_at);
    }
  }

  @Test
  public void testInsertAccountIntoTemp() throws IOException, InterruptedException {
    List<NewAccountFromMigration> accounts = createAccounts();

    jsonManager = new JSONManager("build/test_frs.txt");
    jsonManager.load(tr -> {
    }, accs -> {
      frsInMigration.sendAccounts(accs);
    });


    while (frsDao.get().getTempAccounts().size() == 0) ;

    List<TempAccount> tempAccounts = frsDao.get().getTempAccounts();

    assertThat(tempAccounts).hasSameSizeAs(accounts);

    for (int i = 0; i < tempAccounts.size(); i++) {
      assertThat(tempAccounts.get(i).client_id).isEqualTo(accounts.get(i).client_id);
      assertThat(tempAccounts.get(i).registered_at.toString()).isEqualTo(accounts.get(i).registered_at);
      assertThat(tempAccounts.get(i).account_number).isEqualTo(accounts.get(i).account_number);
    }
  }

  @Test
  public void testInsertAccountIntoReal() throws IOException, InterruptedException, SQLException {
    List<NewAccountFromMigration> accounts = createAccounts();

    jsonManager = new JSONManager("build/test_frs.txt");
    jsonManager.load(tr -> {
    }, accs -> {
      frsInMigration.sendAccounts(accs);
    });

    while (frsDao.get().getTempAccounts().size() == 0) ;

    frsDao.get().insertNewClient(frsDao.get().insertNewCharm());

    frsInMigration.insertTempAccounts();
    List<ClientAccountDot> accountDots = frsDao.get().getAccountDots();

    assertThat(accountDots).hasSameSizeAs(accounts);

    for (int i = 0; i < accountDots.size(); i++) {
      assertThat(accountDots.get(i).number).isEqualTo(accounts.get(i).account_number);
      assertThat(accountDots.get(i).registered_at.toString()).isEqualTo(accounts.get(i).registered_at);
    }
  }

  @Test
  public void testInsertTransactionIntoReal() throws IOException, InterruptedException, SQLException {
    List<TransactionFromMigration> transactions = createTransactions();

    jsonManager = new JSONManager("build/test_frs.txt");
    jsonManager.load(tr -> {
      frsInMigration.sendTransactions(tr);
    }, accounts -> {
    });

    while (frsDao.get().getTempTransactions().size() == 0) ;

    int clientId = frsDao.get().insertNewClient(frsDao.get().insertNewCharm());
    int accId = frsDao.get().insertClientAccount1(clientId);

    frsInMigration.insertTempTransactions();

    List<ClientTransactionDot> transactionDots = frsDao.get().getTransactionsFromReal(accId);

    assertThat(transactionDots).hasSize(1);

    for (int i = 0; i < transactionDots.size(); i++) {
      assertThat(transactionDots.get(i).money).isEqualTo(Double.valueOf(transactions.get(i).money));
      assertThat(transactionDots.get(i).finished_at.toString()).isEqualTo(transactions.get(i).finished_at);
    }
  }

  private List<NewAccountFromMigration> createAccounts() throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("build/test_frs.txt", "UTF-8");
    List<NewAccountFromMigration> accounts = new ArrayList<>();

    NewAccountFromMigration account = new NewAccountFromMigration();
    account.client_id = "1";
    account.registered_at = new Timestamp(new Date().getTime()).toString();
    account.account_number = "1";
    writer.println(account.toJson());
    accounts.add(account);

    NewAccountFromMigration account1 = new NewAccountFromMigration();
    account1.client_id = "2";
    account1.registered_at = new Timestamp(new Date().getTime()).toString();
    account1.account_number = "2";
    writer.println(account1.toJson());
    accounts.add(account1);
    writer.close();
    return accounts;
  }

  private List<TransactionFromMigration> createTransactions() throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("build/test_frs.txt", "UTF-8");
    List<TransactionFromMigration> transactions = new ArrayList<>();
    TransactionFromMigration transaction = new TransactionFromMigration();
    transaction.finished_at = new Timestamp(new Date().getTime()).toString();
    transaction.money = "+100000";
    transaction.account_number = "1";
    transaction.transaction_type = RND.str(10);

    transactions.add(transaction);
    writer.println(transaction.toJson());

    TransactionFromMigration transaction1 = new TransactionFromMigration();
    transaction1.finished_at = new Timestamp(new Date().getTime()).toString();
    transaction1.money = "+100000";
    transaction1.account_number = "2";
    transaction1.transaction_type = RND.str(10);

    transactions.add(transaction1);
    writer.println(transaction1.toJson());
    writer.close();
    return transactions;
  }

  private TransactionFromMigration getRandomTransaction() {
    TransactionFromMigration transaction = new TransactionFromMigration();
    transaction.money = RND.plusDouble(30, 10) + "";
    transaction.account_number = RND.str(20);
    transaction.finished_at = new Timestamp(new Date().getTime()) + "";
    transaction.type = RND.str(10);
    transaction.transaction_type = RND.str(10);
    return transaction;
  }

  private void createTransaction() {
    TransactionFromMigration transaction = new TransactionFromMigration();
  }
}
