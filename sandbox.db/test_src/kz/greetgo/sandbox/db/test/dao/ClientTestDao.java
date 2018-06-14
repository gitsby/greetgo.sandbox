package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface ClientTestDao {

  @Select("SELECT COUNT(*) FROM client")
  Integer count();


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

  @Select("SELECT #{fieldName} FROM client WHERE id=#{clientId}")
  String loadParamValue(@Param("clientId") Integer clientId, @Param("fieldName") String fieldName);
}
