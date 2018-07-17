package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.register_impl.migration.CIAWorkerTest;
import kz.greetgo.sandbox.db.register_impl.migration.FRSWorkerTest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MigrationTestDao {

  @Select("SELECT table_name " +
    "FROM information_schema.tables " +
    "WHERE table_schema='public' " +
    "      AND table_type='BASE TABLE';")
  List<String> getTablesName();

  @Select("SELECT id, error, surname, name, patronymic, gender, birth_date as birthDate, charm FROM ${tableName};")
  List<CIAWorkerTest.TestTmpClient> getTmpClientFromTable(@Param("tableName") String clientTmpTableName);

  @Select("SELECT * FROM ${tmpTable}")
  List<CIAWorkerTest.TestTmpClientPhone> getTmpClientPhones(@Param("tmpTable") String ciaClientPhoneTmpTableName);

  @Select("SELECT * FROM ${tmpTable}")
  List<CIAWorkerTest.TestTmpClientAddress> getTmpClientAddresses(@Param("tmpTable") String clientAddressTmpTableName);

  @Delete("DROP TABLE ${tmpTable}")
  void removeTable(@Param("tmpTable") String tmpTableName);

  @Select("SELECT client_cia_id AS clientId, error, registered_at AS registeredAt, account_number AS accountNumber FROM ${tmpTable}")
  List<FRSWorkerTest.TestTmpClientAccount> getTestTmpClientAccounts(@Param("tmpTable") String tmpTable);

  @Select("SELECT money, error, finished_at AS finishedAt, transaction_type AS transactionType, account_number AS accountNumber FROM ${tmpTable}")
  List<FRSWorkerTest.TestTmpClientAccountTransaction> getTestTmpClientAccountTransactions(@Param("tmpTable") String tmpTable);

  @Insert("INSERT INTO ${table} (client_cia_id, account_number, registered_at) VALUES (#{client_id}, #{account_number}, #{registered_at})")
  void insertClientAccount(@Param("table") String clientAccTableName, @Param("client_id") String clientId,
              @Param("account_number") String accountNumber, @Param("registered_at") String registeredAt);

  @Insert("INSERT INTO ${table}(money, finished_at, transaction_type, account_number) VALUES (#{money}, #{finished_at}, #{transaction_type}, #{account_number})")
  void insertClientAccountTransaction(@Param("table") String clientAccTransTableName, @Param("money") String money,
              @Param("finished_at") String finishedAt, @Param("transaction_type") String transactionType, @Param("account_number") String accountNumber);

  @Insert("CREATE TABLE ${tableName}(client_cia_id VARCHAR(255), error VARCHAR(255), registered_at VARCHAR(255), account_number VARCHAR(255))")
  void createClientAccountTmpTable(@Param("tableName") String clientAccTransTableName);

  @Insert("CREATE TABLE ${tableName} (id VARCHAR(255) NOT NULL, error VARCHAR(255), surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birth_date varchar(255), charm VARCHAR(255))")
  void createClientTmpTable(@Param("tableName") String clientTmpTableName);

  @Delete("CREATE TABLE ${tableName}(money VARCHAR(255), error VARCHAR(255), finished_at VARCHAR(255), transaction_type VARCHAR(255), account_number VARCHAR(255));")
  void createClientAccountTransactionTmpTable(@Param("tableName") String clientAccTableName);

  @Insert("INSERT INTO ${tmpTable} (id, surname, name, patronymic, birth_date, gender, charm) VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{birthDate}, #{gender}, #{charm})")
  void insertClient(@Param("tmpTable") String tmpTable, @Param("id") String id, @Param("surname") String surname, @Param("name") String name,
                    @Param("patronymic") String patronymic, @Param("birthDate") String birthDate, @Param("gender") String gender, @Param("charm") String charm);

  @Insert("CREATE TABLE ${tmpTable} (client VARCHAR(255), error VARCHAR(255), type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))")
  void createClientAddressTmpTable(@Param("tmpTable") String clientAddressTmpTableName);

  @Insert("CREATE TABLE ${tmpTable} (client VARCHAR(255), error VARCHAR(255), type VARCHAR(255), number VARCHAR(255))")
  void createClientPhoneTmpTable(@Param("tmpTable") String clientPhoneTmpTableName);

  @Insert("INSERT INTO ${tmpTable} (client, number, type) VALUES (#{client}, #{number}, #{type})")
  void insertClientPhone(@Param("tmpTable") String clientPhoneTmpTableName, @Param("client") String client, @Param("number") String number, @Param("type") String type);

  @Insert("INSERT INTO ${tmpTable} (client, type, street, house, flat) VALUES (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddress(@Param("tmpTable") String clientAddressTmpTableName, @Param("client") String client, @Param("type") AddressTypeEnum type, @Param("street") String street, @Param("house") String house, @Param("flat") String flat);

  @Select("SELECT id, surname, name, patronymic, gender, birth_date AS birthDate, charm_id AS charmId, cia_id AS ciaId FROM client WHERE actual=1 ORDER BY cia_id;")
  List<Client> getClients();

  @Select("SELECT client, type, street, house, flat FROM client_address WHERE actual=1;")
  List<ClientAddress> getClientAddresses();

  @Select("SELECT client, type, number FROM client_phone WHERE actual=1")
  List<ClientPhone> getClientPhones();

  @Delete("UPDATE client_phone SET actual=0;" +
    "UPDATE client_address SET actual=0;" +
    "UPDATE client SET actual=0;")
  void clearClientTable();

  @Select("INSERT INTO client (actual, cia_id) VALUES (0, #{cia_id});")
  void insertEmptyClient(@Param("cia_id") String cidId);

  @Select("SELECT id, client, money, number, registered_at AS registeredAt FROM client_account WHERE actual = 1;")
  List<ClientAccount> getClientAccounts();

  @Select("SELECT id, account AS accountId, money, finished_at AS finishedAt, type AS typeId FROM client_account_transaction;")
  List<ClientAccountTransaction> getClientAccountTransactions();

  @Delete("UPDATE client_account SET actual = 0;" +
    "DELETE FROM client_account_transaction")
  void clearClientAccountTable();
}