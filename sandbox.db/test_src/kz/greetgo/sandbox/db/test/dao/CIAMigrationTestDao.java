package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration.reader.objects.TempAddress;
import kz.greetgo.sandbox.db.migration.reader.objects.TempClient;
import kz.greetgo.sandbox.db.migration.reader.objects.TempPhone;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CIAMigrationTestDao {

  @Update("create table if not exists temp_client ( \n" +
    "          created_at timestamp not null ,\n" +
    "          client_id varchar(40) primary key,  \n" +
    "          name varchar(30), \n" +
    "          surname varchar(30), \n" +
    "          patronymic varchar(30), \n" +
    "          gender varchar(10), \n" +
    "          birth_date date, \n" +
    "          charm varchar(15), \n" +
    "          error text);")
  void createTempClientTable();

  @Select("insert into characters (name) values ('TestCharm') returning id;")
  int insertNewCharm();

  @Select("insert into client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
    "values ('TEST', 'TEST', 'TEST', 'TEST', current_date, #{charm_id}, '1') returning id;")
  int insertNewClient1(int charm_id);

  @Select("insert into client (name, surname, patronymic, gender, birth_date, charm, migr_client_id)\n" +
    "values ('TEST', 'TEST', 'TEST', 'TEST', current_date, #{charm_id}, '2') returning id;")
  int insertNewClient2(int charm_id);

  @Insert("insert into temp_client(client_id, name, surname, patronymic, gender, birth_date, charm,created_at) " +
    "values(#{client_id}, #{name}, #{surname}, #{patronymic}, #{gender}, current_date, #{charm},current_timestamp)")
  void insertTempClient(TempClient client);

  @Update("drop table if exists temp_client")
  void dropTempClientTable();

  @Update("create table if not exists temp_phone(" +
    "client_id varchar(40)," +
    "number varchar(30)," +
    "type varchar(10), error varchar(100));")
  void createTempPhoneTable();

  @Update("drop table if exists temp_phone")
  void dropTempPhoneTable();

  @Update("create table if not exists temp_address (\n" +
    "  client_id varchar(40),\n" +
    "  street    varchar(100),\n" +
    "  flat      varchar(100),\n" +
    "  house     varchar(100),\n" +
    "  type      varchar(10),error varchar(100),\n" +
    "  PRIMARY KEY (client_id, type)\n" +
    ");")
  void createTempAddressTable();

  @Update("drop table if exists temp_address")
  void dropTempAddressTable();

  @Select("select * from temp_client")
  List<TempClient> getTempClients();

  @Select("select * from temp_phone")
  List<TempPhone> getTempPhones();

  @Select("select * from temp_address order by client_id asc")
  List<TempAddress> getTempAddresses();

  @Select("select * from client order by migr_client_id")
  List<ClientDot> getClientDots();

  @Delete("delete from client where migr_client_id notnull")
  void deleteFromClient();

  @Delete("delete from characters")
  void deleteFromCharms();

  @Select("select * from client_phone order by client_id, type desc")
  List<PhoneDot> getPhonesFromReal();

  @Select("select * from client_address order by client_id asc, type asc")
  List<AddressDot> getAddressDots();

  @Update("alter table client\n" +
    "  add column if not exists migr_client_id varchar(40);\n")
  void createMigrClientIdColumn();
}
