package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.fest.assertions.api.Assertions.assertThat;

public class FRSWorkerTest extends WorkerTest {

  private final String clientAccountTmpTableName = "frs_migration_client_account_tmp";
  private final String clientAccountTransactionTmpTableName = "frs_migration_client_account_transaction_tmp";

  @BeforeMethod
  public void beforeMethod() {
    migrationDao.get().clearClientAccountTable();
  }

  @AfterMethod
  public void afterMethod() {
    removeTmpTables(getFrsTmpTableNames());
  }

  @Test
  public void fillFrsTmpTables() throws Exception {
    Integer randomSize = RND.plusInt(100);
    List<TestClientAccount> leftTestClientAccounts = getRandomTestClientAccounts(randomSize);

    Connection connection = getConnection();
    InputStream inputStream = getInputStream(getFrsTestFileName(), getFrsString(leftTestClientAccounts));

    //
    //
    //
    getFrsWorker(connection, inputStream).fillTmpTables();
    //
    //
    //

    checkInTmpTables(leftTestClientAccounts);

    inputStream.close();
    connection.close();
  }

  @Test
  public void validTmpTables() throws Exception {
    String clientAccountTmpTableName = getNameWithDate(this.clientAccountTmpTableName);
    String clientAccountTransactionTmpTableName = getNameWithDate(this.clientAccountTransactionTmpTableName);

    Integer randomSize = RND.plusInt(100);
    List<TestClientAccount> leftTestClientAccounts = getRandomTestClientAccounts(randomSize);

    {
      toErrorList(leftTestClientAccounts);
      insertToTmpTables(leftTestClientAccounts, clientAccountTmpTableName, clientAccountTransactionTmpTableName);
    }

    Connection connection = getConnection();

    //
    //
    //
    FRSWorker frsWorker = getFrsWorker(connection, null);
    frsWorker.setTmpTableNames(clientAccountTmpTableName, clientAccountTransactionTmpTableName, null);
    frsWorker.validTmpTables();
    //
    //
    //

    checkInTmpTables(leftTestClientAccounts);

    connection.close();
  }

  @Test
  public void margeTmpTables() throws Exception {
    String clientAccountTmpTableName = getNameWithDate(this.clientAccountTmpTableName);
    String clientAccountTransactionTmpTableName = getNameWithDate(this.clientAccountTransactionTmpTableName);

    Integer randomSize = RND.plusInt(100);
    List<TestClientAccount> leftTestClientAccounts = getRandomTestClientAccounts(randomSize);


    toNotMargeList(leftTestClientAccounts);
    insertToTmpTables(leftTestClientAccounts, clientAccountTmpTableName, clientAccountTransactionTmpTableName);

    Connection connection = getConnection();

    //
    //
    //
    FRSWorker frsWorker = getFrsWorker(connection, null);
    frsWorker.setTmpTableNames(clientAccountTmpTableName, clientAccountTransactionTmpTableName, null);
    frsWorker.margeTmpTables();
    //
    //
    //

    margeList(leftTestClientAccounts);
    removeInvalidClientAccounts(leftTestClientAccounts);
    checkInTmpTables(leftTestClientAccounts);

    connection.close();
  }

  @Test
  public void migrateToTables() throws Exception {
    Integer randomSize = RND.plusInt(10);
    String randomCiaId = RND.str(10);

    {
      migrationDao.get().insertEmptyClient(randomCiaId);
    }

    List<TestClientAccount> leftTestClientAccounts = getRandomTestClientAccounts(randomSize);
    leftTestClientAccounts.forEach(testClientAccount -> testClientAccount.tmpClientAccount.clientId = randomCiaId);

    toNotMargeList(leftTestClientAccounts);
    toErrorList(leftTestClientAccounts);

    Connection connection = getConnection();
    File tmpFile = createTmpFile(getFrsTestFileName(), getFrsString(leftTestClientAccounts));

    //
    //
    //
    migration.get().migrate(connection, tmpFile);
    //
    //
    //

    margeList(leftTestClientAccounts);
    leftTestClientAccounts = removeInvalidClientAccounts(leftTestClientAccounts);

    checkInTables(leftTestClientAccounts);

    tmpFile.delete();
    connection.close();
  }

  private List<TestClientAccount>  removeInvalidClientAccounts(List<TestClientAccount> leftTestClientAccounts) {
    List<TestClientAccount> clientAccounts = Lists.newArrayList();
    for (TestClientAccount clientAccount : leftTestClientAccounts)
      if (clientAccount.tmpClientAccount.accountNumber != null) {
        clientAccounts.add(clientAccount);
        clientAccount.clientAccountTransactions = removeInvalidClientAccountTransactions(clientAccount.clientAccountTransactions);
      }
    return clientAccounts;
  }

  private List<TestTmpClientAccountTransaction> removeInvalidClientAccountTransactions(List<TestTmpClientAccountTransaction> clientAccountTransactions) {
    List<TestTmpClientAccountTransaction> accountTransactions = Lists.newArrayList();
    for (TestTmpClientAccountTransaction accountTransaction : clientAccountTransactions)
      if (accountTransaction.accountNumber != null)
        accountTransactions.add(accountTransaction);
    return accountTransactions;
  }

  private void checkInTables(List<TestClientAccount> leftTestClientAccounts) {
    List<ClientAccount> clientAccounts = migrationDao.get().getClientAccounts();
    List<ClientAccountTransaction> clientAccountTransactions = migrationDao.get().getClientAccountTransactions();

    assertThat(clientAccounts).hasSize(leftTestClientAccounts.size());
    assertThat(clientAccountTransactions).hasSize(getAccountTransactionsSize(leftTestClientAccounts));
  }

  private void margeList(List<TestClientAccount> leftTestClientAccounts) {
    leftTestClientAccounts.forEach(testClientAccount -> {
      List<TestTmpClientAccountTransaction> accountTransactions = Lists.newArrayList();
      for (TestTmpClientAccountTransaction accountTransaction : testClientAccount.clientAccountTransactions) {
        TestTmpClientAccountTransaction include = include(accountTransactions, accountTransaction);
        if (include == null) {
          accountTransactions.add(accountTransaction);
          continue;
        }
        if (accountTransaction.transactionType != null) include.transactionType = accountTransaction.transactionType;
      }
      testClientAccount.clientAccountTransactions = accountTransactions;
    });
  }

  private TestTmpClientAccountTransaction include(List<TestTmpClientAccountTransaction> accountTransactions, TestTmpClientAccountTransaction accountTransaction) {
    for (TestTmpClientAccountTransaction testAccountTransaction : accountTransactions)
      if (Objects.equals(testAccountTransaction.finishedAt, accountTransaction.finishedAt)
        && Objects.equals(testAccountTransaction.money, accountTransaction.money)
        && Objects.equals(testAccountTransaction.accountNumber, accountTransaction.accountNumber)) return testAccountTransaction;
    return null;
  }

  private void toNotMargeList(List<TestClientAccount> leftTestClientAccounts) {
    leftTestClientAccounts.forEach(testClientAccount -> toMargeAccountList(testClientAccount.clientAccountTransactions));
  }

  private void toMargeAccountList(List<TestTmpClientAccountTransaction> accountTransactions) {
    String finishedAt = null;
    String money = null;
    for (TestTmpClientAccountTransaction accountTransaction : accountTransactions) {
      if (money == null && finishedAt == null) {
        finishedAt = accountTransaction.finishedAt;
        money = accountTransaction.money;
      } else if (RND.bool()) {
        accountTransaction.money = money;
        accountTransaction.finishedAt = finishedAt;
        money = finishedAt = null;
      }
    }
  }

  private void checkInTmpTables(List<TestClientAccount> leftTestClientAccounts) {
    List<String> frsTmpTableNames = getFrsTmpTableNames();

    List<TestTmpClientAccount> tmpClientAccounts = getTestTmpClientAccounts(getTmpClientAccountTableName(getFrsTmpTableNames()));
    List<TestTmpClientAccountTransaction> tmpClientAccountTransactions = getClientAccountsTransactions(getClientAccountTransactionsTableName(frsTmpTableNames));

    System.out.println(leftTestClientAccounts);
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println(tmpClientAccounts);
    System.out.println(tmpClientAccountTransactions);

    assertThat(tmpClientAccounts).hasSize(leftTestClientAccounts.size());
    assertThat(tmpClientAccountTransactions).hasSize(getAccountTransactionsSize(leftTestClientAccounts));

    for (int i = 0; i < leftTestClientAccounts.size(); i++) {
      isEqual(tmpClientAccounts.get(i), leftTestClientAccounts.get(i).tmpClientAccount);
      tmpClientAccountTransactions.forEach(transaction -> transaction.money = String.valueOf(Float.parseFloat(transaction.money)));
      for (TestTmpClientAccountTransaction clientAccountTransaction : leftTestClientAccounts.get(i).clientAccountTransactions) {
        clientAccountTransaction.money = String.valueOf(Float.parseFloat(clientAccountTransaction.money));
        assertThat(tmpClientAccountTransactions).contains(clientAccountTransaction);
      }
    }
  }

  private int getAccountTransactionsSize(List<TestClientAccount> leftTestClientAccounts) {
    int count = 0;
    for (TestClientAccount testClientAccount : leftTestClientAccounts)
      count += testClientAccount.clientAccountTransactions.size();
    return count;
  }

  private void insertToTmpTables(List<TestClientAccount> clientAccounts, String clientAccTableName, String clientAccTransTableName) {
    createClientAccountTmpTable(clientAccTableName);
    createClientAccountTransactionTmpTable(clientAccTransTableName);
    for (TestClientAccount clientAccount : clientAccounts) {
      insertTmpClientAccount(clientAccTableName, clientAccount.tmpClientAccount);
      for (TMPClientAccountTransaction accountTransaction : clientAccount.clientAccountTransactions)
        insertTmpClientAccountTransaction(clientAccTransTableName, accountTransaction);
    }
  }

  private void createClientAccountTransactionTmpTable(String clientAccTransTableName) {
    migrationDao.get().createClientAccountTransactionTmpTable(clientAccTransTableName);
  }

  private void createClientAccountTmpTable(String clientAccTableName) {
    migrationDao.get().createClientAccountTmpTable(clientAccTableName);
  }

  private void insertTmpClientAccount(String tmpTableName, TMPClientAccount clientAccount) {
    migrationDao.get().insertClientAccount(tmpTableName, clientAccount.clientId,
      clientAccount.accountNumber, clientAccount.registeredAt);
  }

  private void insertTmpClientAccountTransaction(String tmpTableName, TMPClientAccountTransaction accountTransaction) {
    migrationDao.get().insertClientAccountTransaction(tmpTableName, accountTransaction.money,
      accountTransaction.finishedAt, accountTransaction.transactionType, accountTransaction.accountNumber);
  }

  private void toErrorList(List<TestClientAccount> testClientAccounts) {
    testClientAccounts.forEach(tca -> {
      if (RND.bool()) {
        tca.tmpClientAccount.accountNumber = null;
        tca.tmpClientAccount.error = MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND;
      }
      tca.clientAccountTransactions.forEach(
        at -> {
          if (RND.bool()) {
            at.accountNumber = null;
            at.error = MigrationError.FRS.ACCOUNT_NUMBER_NOT_FOUND;
          }
        }
      );
    });
  }

  private String getFrsTestFileName() {
    return getNameWithDate("frs_test")+".json_row.txt";
  }

  private void isEqual(TMPClientAccount tmpClientAccount, TMPClientAccount clientAccount) {
    assertThat(tmpClientAccount).isEqualsToByComparingFields(clientAccount);
  }

  private List<TestTmpClientAccountTransaction> getClientAccountsTransactions(String tmpClientAccountTransactionsTableName) {
    return migrationDao.get().getTestTmpClientAccountTransactions(tmpClientAccountTransactionsTableName);
  }

  private String getClientAccountTransactionsTableName(List<String> frsTmpTableNames) {
    return frsTmpTableNames.stream().filter(name -> name.startsWith(clientAccountTransactionTmpTableName)).findFirst().get();
  }

  private List<TestTmpClientAccount> getTestTmpClientAccounts(String tmpClientAccountsTableName) {
    return migrationDao.get().getTestTmpClientAccounts(tmpClientAccountsTableName);
  }

  private String getTmpClientAccountTableName(List<String> frsTmpTableNames) {
    Stream<String> stream = frsTmpTableNames.stream().filter(name -> name.startsWith(clientAccountTmpTableName) &&
      !name.startsWith(clientAccountTransactionTmpTableName));
    return stream.findFirst().get();
  }

  private String getFrsString(List<TestClientAccount> testClientAccounts) {
    StringBuilder sb = new StringBuilder();
    testClientAccounts.forEach(testClientAccount -> sb.append(testClientAccount.toJson()));
    return sb.toString();
  }

  private List<TestClientAccount> getRandomTestClientAccounts(int count) {
    List<TestClientAccount> testClientAccounts = Lists.newArrayList();
    for (int i = 0; i < count; i++) testClientAccounts.add(getRandomTestClientAccount());
    return testClientAccounts;
  }

  private TestClientAccount getRandomTestClientAccount() {
    TestClientAccount testClientAccount = new TestClientAccount();
    testClientAccount.tmpClientAccount = getRandomTestTmpClientAccount();
    testClientAccount.clientAccountTransactions = getRandomTestTmpClientTransactions(RND.plusInt(10)+1, testClientAccount.tmpClientAccount.accountNumber);
    return testClientAccount;
  }

  private List<TestTmpClientAccountTransaction> getRandomTestTmpClientTransactions(int count, String accountNumber) {
    List<TestTmpClientAccountTransaction> clientAccountTransactions = Lists.newArrayList();
    for (int i = 0; i < count; i++) clientAccountTransactions.add(getRandomTestTmpClientTransaction(accountNumber));
    return clientAccountTransactions;
  }

  private TestTmpClientAccountTransaction getRandomTestTmpClientTransaction(String accountNumber) {
    TestTmpClientAccountTransaction clientAccountTransaction = new TestTmpClientAccountTransaction();
    clientAccountTransaction.accountNumber = accountNumber;
    clientAccountTransaction.transactionType = RND.str(10);
    clientAccountTransaction.money = getRandomMoney();
    clientAccountTransaction.finishedAt = getRandomDate("yyyy-MM-dd");
    return clientAccountTransaction;
  }

  private String getRandomMoney() {
    StringBuilder sb = new StringBuilder();
    sb.append(RND.bool()?"+":"-").append(RND.plusInt(8)+1);
    for(int i = 0; i < RND.plusInt(10); i++) sb.append(RND.plusInt(9));
    sb.append(".");
    for (int i = 1; i <= RND.plusInt(3)+1; i++) sb.append(RND.plusInt(9));
    return sb.toString();
  }

  private TestTmpClientAccount getRandomTestTmpClientAccount() {
    TestTmpClientAccount clientAccount = new TestTmpClientAccount();
    clientAccount.clientId = RND.str(10);
    clientAccount.accountNumber = RND.str(10);
    clientAccount.registeredAt = getRandomDate("yyyy-MM-dd");
    return clientAccount;
  }

  public static class TestTmpClientAccount extends TMPClientAccount {
    String error = null;

    @Override
    public boolean equals(Object obj) {
      if (obj == null) return false;
      if (!(obj instanceof TestTmpClientAccount)) return false;
      TestTmpClientAccount o = (TestTmpClientAccount) obj;
      return Objects.equals(o.accountNumber, accountNumber) && Objects.equals(o.error, error)
        && Objects.equals(o.clientId, clientId);
    }

    String toJson() {
      return "{" +
        "\"client_id\":\"" + clientId + '\"' +
        ", \"registered_at\":\"" + registeredAt + '\"' +
        ", \"account_number\":\"" + accountNumber + '\"' +
        ", \"type\":\"new_account\"" +
        "}\n";
    }

    @Override
    public String toString() {
      return toJson();
    }
  }

  public static class TestTmpClientAccountTransaction extends TMPClientAccountTransaction {
    String error = null;

    @Override
    public boolean equals(Object obj) {
      if (obj == null) return false;
      if (!(obj instanceof TestTmpClientAccountTransaction)) return false;
      TestTmpClientAccountTransaction o = (TestTmpClientAccountTransaction) obj;
      return Objects.equals(o.error, error) && Objects.equals(o.accountNumber, accountNumber)
        && Objects.equals(o.transactionType, transactionType) && Objects.equals(o.finishedAt, finishedAt)
        && Objects.equals(o.money, money);
    }

    String toJson() {
      return "{" +
        "  \"money\":\"" + money + '\"' +
        ", \"finished_at\":\"" + finishedAt + '\"' +
        ", \"transaction_type\":\"" + transactionType + '\"' +
        ", \"account_number\":\"" + accountNumber + '\"' +
        ", \"type\":\"transaction\"" +
        "}\n";
    }

    @Override
    public String toString() {
      return toJson();
    }
  }

  class TestClientAccount {
    TestTmpClientAccount tmpClientAccount;
    List<TestTmpClientAccountTransaction> clientAccountTransactions;

    String toJson() {
      StringBuilder sb = new StringBuilder();
      if (tmpClientAccount != null) sb.append(tmpClientAccount.toJson()).append('\n');
      clientAccountTransactions.forEach(tmp -> sb.append(tmp.toJson()).append('\n'));
      return sb.toString();
    }

    @Override
    public String toString() {
      return toJson();
    }
  }
}
