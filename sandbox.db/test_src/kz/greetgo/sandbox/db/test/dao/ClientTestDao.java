package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface ClientTestDao {

  @Select("SELECT COUNT(*) FROM client WHERE actual=1")
  Integer count();

  @Select("SELECT * FROM client_address WHERE client=#{client} AND type=#{type}")
  ClientAddress getAddress(@Param("client") Integer client, @Param("type") AddressTypeEnum type);

  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm});")
  void insertClient(@Param("id") Integer id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") Date birthDate,
                    @Param("charm") Integer charm);

  @Insert("INSERT INTO charm(id, name, description, energy) VALUES (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") Integer id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") Float energy);

  @Insert("INSERT INTO client_address (client, type, street, house, flat) "+
    "VALUES (#{client}, #{type}, #{street}, #{house}, #{flat});")
  void insertClientAddress(@Param("client") Integer client,
                           @Param("type") AddressTypeEnum type,
                           @Param("street") String street,
                           @Param("house") String house,
                           @Param("flat") String flat);

  @Insert("INSERT INTO client_phone (client, number, type) " +
    "VALUES (#{client}, #{number}, #{type});")
  void insertClientPhone(@Param("client") Integer client,
                         @Param("number") String number,
                         @Param("type") PhoneType type);

  @Select("SELECT ${fieldName} FROM client WHERE id=#{clientId}")
  String loadParamValue(@Param("clientId") Integer clientId, @Param("fieldName") String fieldName);

  @Insert("INSERT INTO client_account(client, number, money, registered_at) VALUES(#{client}, #{number}, #{money}, #{registered_at})")
  void insertClientAccount(@Param("client") Integer client,
                           @Param("number") String number,
                           @Param("money") float money,
                           @Param("registered_at") Date registeredAt);

  @Select("SELECT * FROM client_account WHERE client=#{id}")
  List<ClientAccount> getClientAccounts(@Param("id") Integer id);

  @Select("SELECT * FROM client")
  List<Client> details();

  @Delete("DELETE FROM client_account; " +
    "DELETE FROM client_address; " +
      "DELETE FROM client_phone; " +
      "DELETE FROM client; " +
      "DELETE FROM charm")
  void createAllTables();
}
