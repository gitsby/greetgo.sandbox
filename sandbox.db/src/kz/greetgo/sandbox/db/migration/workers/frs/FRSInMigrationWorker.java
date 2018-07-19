package kz.greetgo.sandbox.db.migration.workers.frs;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.workers.SqlWorker;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class FRSInMigrationWorker extends SqlWorker {

  PreparedStatement accountsStatement;
  PreparedStatement transactionStatement;

  public FRSInMigrationWorker(Connection connection) throws SQLException {
    super(connection);
    accountsStatement = connection.prepareStatement("insert into temp_account(account_number, client_id, registered_at) values(?,?,?)");
    transactionStatement = connection.prepareStatement("insert into temp_transaction(account_number, money, transaction_type, finished_at) values(?,?,?,?)");

  }

  public void prepare() throws SQLException {
    exec("create table if not exists temp_transaction(" +
      "finished_at timestamp," +
      "account_number varchar(100), " +
      "money float, " +
      "transaction_type varchar(200));");

    exec("create table if not exists temp_account (\n" +
      "  account_number varchar(100),\n" +
      "  client_id      varchar(40),\n" +
      "  registered_at  timestamp\n" +
      "  error varchar(100)," +
      ");");

    connection.commit();
  }


  public void sendTransactions(List<TransactionFromMigration> transactions) throws SQLException, ParseException {
    for (int i = 0; i < transactions.size(); i++) {
      TransactionFromMigration transactionFromMigration = transactions.get(i);
      batchInsert(transactionStatement, transactionFromMigration.account_number,
        Float.valueOf(transactions.get(0).money.replace("_", ""))
        , transactionFromMigration.transaction_type
        , timeStampFromString(transactionFromMigration.finished_at.replace("T", " ")));

      if (i % 1000 == 0) {
        transactionStatement.executeBatch();
      }
    }

    transactionStatement.executeBatch();
    connection.commit();
  }

  public void sendAccounts(List<NewAccountFromMigration> accounts) throws SQLException, ParseException {
    for (int i = 0; i < accounts.size(); i++) {
      NewAccountFromMigration acc = accounts.get(i);
      batchInsert(accountsStatement, acc.account_number, acc.client_id, timeStampFromString(acc.registered_at.replace("T", " ")));
    }

    accountsStatement.executeBatch();
    connection.commit();
  }

  public void dropTempTables() throws SQLException {
    accountsStatement.close();
    transactionStatement.close();
  }

  public void updateError() throws SQLException, IOException {
    exec("update temp_account\n" +
      "set error = 'No account number;'\n" +
      "where account_number = 'null' or account_number isnull';");

    exec("update temp_account\n" +
      "set error = 'No client_id'\n" +
      "where client_id not in (select temp_client.client_id\n" +
      "                        from temp_client);");

    exec("update temp_transaction\n" +
      "set error = 'No account number;'\n" +
      "where account_number not in (select temp_account.account_number\n" +
      "                             from temp_account);");

    exec("update temp_transaction\n" +
      "set error = 'No transaction type;'\n" +
      "where transaction_type = 'null';\n");

    CopyManager copyManager = new CopyManager((BaseConnection) connection);
    new File("build").mkdirs();

    copyManager.copyOut("COPY (select * from temp_transaction where error!='' and error notnull) to STDOUT ", new PrintWriter("build/error_frs_transaction.csv", "UTF-8"));
    copyManager.copyOut("COPY (select * from temp_account where error!='' and error notnull) to STDOUT ", new PrintWriter("build/error_frs_account.csv", "UTF-8"));

    connection.commit();
  }


  public void insertIntoAccount() throws SQLException {

    exec("insert into client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
      "  select\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    current_date,\n" +
      "    (select id\n" +
      "     from characters\n" +
      "     limit 1),\n" +
      "    temp_account.client_id\n" +
      "  from temp_account\n" +
      "  where temp_account.error='No account number;';");

    exec("insert into client_account (client_id, registered_at, number)\n" +
      "  select\n" +
      "    client.id,\n" +
      "    temp_account.registered_at,\n" +
      "    temp_account.account_number\n" +
      "  from temp_account, client\n" +
      "  where temp_account.client_id = client.migr_client_id;");
    connection.commit();
  }

  public void insertIntoTransaction() throws SQLException {
    exec("insert into transaction_type (name) select temp_transaction.transaction_type\n" +
      "                                    from temp_transaction\n" +
      "                                    where transaction_type notnull and transaction_type != 'null'\n" +
      "                                    group by temp_transaction.transaction_type;");

    exec("insert into client_account_transaction (account, money, finished_at, type)\n" +
      "  select\n" +
      "    trans.id,\n" +
      "    trans.money,\n" +
      "    trans.finished_at,\n" +
      "    transaction_type.id\n" +
      "  from (select\n" +
      "          client_account.id,\n" +
      "          temp_transaction.money,\n" +
      "          temp_transaction.finished_at,\n" +
      "          temp_transaction.transaction_type\n" +
      "        from temp_transaction, client_account\n" +
      "        where temp_transaction.account_number = client_account.number) as trans, transaction_type\n" +
      "  where trans.transaction_type = transaction_type.name;");

    exec("update client_account\n" +
      "set money = (\n" +
      "  (select sum(client_account_transaction.money) as total\n" +
      "   from client_account as accs\n" +
      "     join client_account_transaction\n" +
      "       on client_account.client_id = client_account_transaction.account\n" +
      "   where accs.client_id = client_account.client_id\n" +
      "   group by client_account.client_id)\n" +
      ");");
    connection.commit();
  }

}
