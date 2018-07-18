package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
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


  @Insert("insert into temp_client(client_id, name, surname, patronymic, gender, birth_date, charm,created_at) " +
    "values(#{client_id}, #{name}, #{surname}, #{patronymic}, #{gender}, current_date, #{charm},current_timestamp)")
  void insertTempClient(ClientFromMigration client);

  @Update("drop table if exists temp_client")
  void dropTempClientTable();

  @Update("create table if not exists temp_phone(" +
    "client_id varchar(40)," +
    "number varchar(30)," +
    "type varchar(10));")
  void createTempPhoneTable();

  @Update("drop table if exists temp_phone")
  void dropTempPhoneTable();

  @Update("create table if not exists temp_address (\n" +
    "  client_id varchar(40),\n" +
    "  street    varchar(100),\n" +
    "  flat      varchar(100),\n" +
    "  house     varchar(100),\n" +
    "  type      varchar(10),\n" +
    "  PRIMARY KEY (client_id, type)\n" +
    ");")
  void createTempAddressTable();

  @Update("drop table if exists temp_address")
  void dropTempAddressTable();

  @Select("select * from temp_client")
  List<ClientFromMigration> getTempClients();

  @Select("select * from temp_phone")
  List<PhoneFromMigration> getTempPhones();

  @Select("select * from temp_address order by client_id asc")
  List<AddressFromMigration> getTempAddresses();

  @Select("select * from client order by migr_client_id")
  List<ClientDot> getClientDots();

  @Delete("delete from client where migr_client_id notnull")
  void deleteFromClient();

  @Delete("delete from characters")
  void deleteFromCharms();

  @Select("select * from client_phone")
  List<PhoneDot> getPhonesFromReal();

  @Select("select * from client_address order by client_id")
  List<AddressDot> getAddressDots();

  @Update("alter table client\n" +
    "  add column if not exists migr_client_id varchar(40);\n")
  void createMigrClientIdColumn();
}
