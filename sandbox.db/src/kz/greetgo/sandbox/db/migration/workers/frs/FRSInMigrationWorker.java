package kz.greetgo.sandbox.db.migration.workers.frs;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;
import kz.greetgo.sandbox.db.migration.workers.SqlWorker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class FRSInMigrationWorker extends SqlWorker {

  public FRSInMigrationWorker(Connection connection) {
    super(connection);
  }

  public void prepare() throws SQLException {
    exec("create table temp_transaction(" +
      "finished_at timestamp," +
      "account_number varchar(100), " +
      "money float, " +
      "transaction_type varchar(200));");

    exec("create table temp_account (\n" +
      "  account_number varchar(100),\n" +
      "  client_id      varchar(40),\n" +
      "  registered_at  timestamp\n" +
      ");");

    connection.commit();
  }


  public void sendTransactions(List<TransactionFromMigration> transactions) throws SQLException, ParseException {
    PreparedStatement statement = connection.prepareStatement("insert into temp_transaction(account_number, money, transaction_type, finished_at) values(?,?,?,?)");
    for (int i = 0; i < transactions.size(); i++) {
      TransactionFromMigration transactionFromMigration = transactions.get(i);
      batchInsert(statement, transactionFromMigration.account_number,
        Float.valueOf(transactions.get(0).money.replace("_", ""))
        , transactionFromMigration.transaction_type
        , timeStampFromString(transactionFromMigration.finished_at.replace("T", " ")));
    }

    statement.executeBatch();
    connection.commit();
  }

  public void sendAccounts(List<NewAccountFromMigration> accounts) throws SQLException, ParseException {
    PreparedStatement statement = connection.prepareStatement("insert into temp_account(account_number, client_id, registered_at) values(?,?,?)");
    for (int i = 0; i < accounts.size(); i++) {
      NewAccountFromMigration acc = accounts.get(i);
      batchInsert(statement, acc.account_number, acc.client_id, timeStampFromString(acc.registered_at.replace("T", " ")));
    }

    statement.executeBatch();
    connection.commit();
  }

  public void dropTempTables() throws SQLException {
    connection.close();
  }

  public void insertIntoAccount() throws SQLException {
    exec("insert into client_account (client_id, registered_at, number)\n" +
      "  select\n" +
      "    client.client_id,\n" +
      "    temp_account.registered_at,\n" +
      "    temp_account.account_number\n" +
      "  from temp_account, client\n" +
      "  where temp_account.client_id = client.migr_client_id;");
    exec("insert into client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
      "  select\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    'NoAcc',\n" +
      "    current_date,\n" +
      "    (select client_id\n" +
      "     from characters\n" +
      "     limit 1),\n" +
      "    temp_account.client_id\n" +
      "  from temp_account\n" +
      "  where temp_account.client_id\n" +
      "        not in\n" +
      "        (select migr_client_id\n" +
      "         from client);");
    connection.commit();
  }

  public void insertIntoTransaction() throws SQLException {
    exec("insert into transaction_type (name) select temp_transaction.transaction_type\n" +
      "                                    from temp_transaction\n" +
      "                                    where transaction_type notnull and transaction_type != 'null'\n" +
      "                                    group by temp_transaction.transaction_type;");

    exec("insert into client_account_transaction (account, money, finished_at, type)\n" +
      "  select\n" +
      "    trans.client_id,\n" +
      "    trans.money,\n" +
      "    trans.finished_at,\n" +
      "    transaction_type.client_id\n" +
      "  from (select\n" +
      "          client_account.client_id,\n" +
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
