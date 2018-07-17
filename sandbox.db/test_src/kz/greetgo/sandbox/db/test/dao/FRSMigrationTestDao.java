package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Update;

public interface FRSMigrationTestDao {

  @Update("create table if not exists temp_transaction(" +
    "finished_at timestamp," +
    "account_number varchar(100), " +
    "money float, " +
    "transaction_type varchar(200));")
  void createTempTransactionTable();

  @Update("create table if not exists temp_account (\n" +
    "  account_number varchar(100),\n" +
    "  client_id      varchar(40),\n" +
    "  registered_at  timestamp\n" +
    ");")
  void createTempAccountTable();

  @Update("drop table if exists temp_transaction")
  void dropTransactionTable();

  @Update("drop table if exists temp_account")
  void dropAccountTable();

}
