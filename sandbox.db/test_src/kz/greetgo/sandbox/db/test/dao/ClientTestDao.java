package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface ClientTestDao {

  //language=PostgreSQL
  @Insert("INSERT INTO client (id, surname, name, patronymic, gender, birth_date, charm_id) " +
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm});")
  void insertClient(@Param("id") Integer id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") GenderEnum gender,
                    @Param("birth_date") Date birthDate,
                    @Param("charm") Integer charmId);
  //language=PostgreSQL
  @Insert("INSERT INTO charm(id, name, description, energy) VALUES (#{id}, #{name}, #{description}, #{energy})")
  void insertCharm(@Param("id") Integer id,
                   @Param("name") String name,
                   @Param("description") String description,
                   @Param("energy") Float energy);

  //language=PostgreSQL
  @Insert("INSERT INTO client_address (client, type, street, house, flat) " +
    "VALUES (#{client}, #{type}, #{street}, #{house}, #{flat});")
  void insertClientAddress(@Param("client") Integer client,
                           @Param("type") AddressTypeEnum type,
                           @Param("street") String street,
                           @Param("house") String house,
                           @Param("flat") String flat);

  //language=PostgreSQL
  @Insert("INSERT INTO client_phone (client, number, type) " +
    "VALUES (#{client}, #{number}, #{type});")
  void insertClientPhone(@Param("client") Integer client,
                         @Param("number") String number,
                         @Param("type") PhoneType type);


  //language=PostgreSQL
  @Insert("INSERT INTO client_account(client, number, money, registered_at) VALUES(#{client}, #{number}, #{money}, #{registered_at})")
  void insertClientAccount(@Param("client") Integer client,
                           @Param("number") String number,
                           @Param("money") float money,
                           @Param("registered_at") Date registeredAt);

  //language=PostgreSQL
  @Select("SELECT id, client, money, number, registered_at AS registeredAt FROM client_account WHERE client=#{id}")
  List<ClientAccount> getClientAccounts(@Param("id") Integer id);

  //language=PostgreSQL
  @Delete("UPDATE client SET actual=0;" +
    "UPDATE client_phone SET actual=0;" +
    "UPDATE client_address SET actual=0;" +
    "UPDATE client_account SET actual=0;" +
    "TRUNCATE charm CASCADE;")
  void clearAllTables();

  //language=PostgreSQL
  @Select("SELECT id, surname, name, patronymic, gender, birth_date as birthDate, charm_id as charmId FROM client")
  List<Client> getClients();

  //language=PostgreSQL
  @Select("SELECT * FROM client_address WHERE client=#{clientId} AND type=#{type}")
  ClientAddress getClientAddress(@Param("clientId") Integer clientId, @Param("type") AddressTypeEnum fact);

  //language=PostgreSQL
  @Select("SELECT * FROM client_phone WHERE client=#{clientId} AND type=#{type} AND actual=1")
  ClientPhone getClientPhone(@Param("clientId") Integer clientId, @Param("type") PhoneType home);

  //language=PostgreSQL
  @Select("SELECT actual FROM client WHERE id=#{clientId}")
  Integer getActual(Integer rClientId);
}
