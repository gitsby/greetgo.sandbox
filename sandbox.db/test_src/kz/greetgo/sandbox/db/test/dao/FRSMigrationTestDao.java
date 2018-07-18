package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.classes.TempAccount;
import kz.greetgo.sandbox.db.classes.TempTransaction;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientTransactionDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

  @Select("select * from temp_transaction")
  List<TempTransaction> getTempTransactions();

  @Select("select * from temp_account")
  List<TempAccount> getTempAccounts();

  @Select("insert into client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
    "values ('TEST', 'TEST', 'TEST', 'TEST', current_date, #{charm_id}, '1') returning id;")
  int insertNewClient(int charm_id);

  @Select("insert into client_account (client_id, number) values(#{client_id},'1') returning id")
  int insertClientAccount1(int client_id);

  @Select("insert into client_account (client_id, number) values(#{client_id},'2') returning id")
  int insertClientAccount2(int client_id);

  @Select("select * from client_account_transaction where account=#{id}")
  List<ClientTransactionDot> getTransactionsFromReal(int id);

  @Select("select * from client_account")
  List<ClientAccountDot> getAccountDots();

  @Select("insert into characters (name) values ('TestCharm') returning id;")
  int insertNewCharm();

  @Delete("delete from client where migr_client_id notnull")
  void deleteClients();

}
