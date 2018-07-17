package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
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

  @Select("select * from temp_address")
  List<AddressFromMigration> getAddresses();
}
