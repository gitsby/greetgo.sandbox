package kz.greetgo.sandbox.db.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Bean
public interface ClientDao {

  /** SELECTS */

  @Select("SELECT * FROM client WHERE id=#{clientId} AND actual=1")
  Client get(@Param("clientId") Integer clientId);

  @Select("SELECT * FROM client_address WHERE client=#{clientId} AND type=#{type} AND actual=1")
  ClientAddress getAddress(@Param("clientId") Integer clientId, @Param("type") AddressTypeEnum type);

  @Select("SELECT * FROM client_phone WHERE client=#{clientId} AND type=#{type} AND actual=1")
  ClientPhone getPhone(@Param("clientId") Integer clientId, @Param("type")PhoneType type);

  @Select("SELECT * FROM charm WHERE id=#{id}")
  Charm getCharm(@Param("id") Integer charmId);

  /** INSERT */

  @Select("INSERT INTO client(surname, name, patronymic, gender, birth_date, charm) "+
    "VALUES (#{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}) RETURNING id;")
  Integer insert(@Param("surname") String surname,
                 @Param("name") String name,
                 @Param("patronymic") String patronymic,
                 @Param("gender") Gender gender,
                 @Param("birth_date") Date birthDate,
                 @Param("charm") Integer charm);

  @Insert("INSERT INTO client(id, surname, name, patronymic, gender, birth_date, charm) "+
    "VALUES (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm});")
  void insertWithId(@Param("id") Integer id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_date") Date birthDate,
                    @Param("charm") Integer charm);

  @Insert("INSERT INTO client_address(client, type, street, house, flat) "+
    "VALUES (#{client}, #{type}, #{street}, #{house}, #{flat})")
  void insertAddress(@Param("client") Integer client,
                     @Param("type") AddressTypeEnum type,
                     @Param("street") String street,
                     @Param("house") String house,
                     @Param("flat") String flat);

  @Insert("INSERT INTO client_phone(client, type, number) "+
    "VALUES (#{client}, #{type}, #{number})")
  void insertPhone(@Param("client") Integer client,
                   @Param("type") PhoneType type,
                   @Param("number") String number);


  /** UPDATES */

  @Update("UPDATE client_address SET ${fieldName}=#{fieldValue} WHERE client=#{client} AND type=#{type}")
  void updateAddressField(@Param("client") Integer clientId,
                          @Param("type") AddressTypeEnum type,
                          @Param("fieldName") String fieldName,
                          @Param("fieldValue") Object fieldValue);

  @Update("UPDATE client_phone SET ${fieldName}=#{fieldValue} WHERE client=#{client} AND number=#{number}")
  void updatePhoneField(@Param("client") Integer clientId,
                        @Param("number") String number,
                        @Param("fieldName") String fieldName,
                        @Param("fieldValue") Object fieldValue);

  @Update("UPDATE client_phone SET number=#{number}, type=#{type} WHERE client=#{client} AND number=#{oldNumber}")
  void updatePhone(@Param("client") Integer client,
                   @Param("oldNumber") String oldNumber,
                   @Param("type") PhoneType type,
                   @Param("number") String number);

  @Update("UPDATE client_address SET street=#{street}, house=#{house}, flat=#{flat} WHERE client=#{client} AND type=#{type}")
  void updateAddress(@Param("client") Integer client,
                     @Param("type") AddressTypeEnum type,
                     @Param("street") String street,
                     @Param("house") String house,
                     @Param("flat") String flat);

  @Update("UPDATE client SET name=#{name}, surname=#{surname}, patronymic=#{patronymic}, gender=#{gender}, birth_date=#{birth_date}, charm=#{charm} WHERE id=#{id}")
  void update(@Param("id") Integer clientId,
              @Param("surname") String surname,
              @Param("name") String name,
              @Param("patronymic") String patronymic,
              @Param("gender") Gender gender,
              @Param("birth_date") Date birth_date,
              @Param("charm") Integer charm);

  @Update("UPDATE client SET ${fieldName}=#{fieldValue} WHERE id=#{id}")
  void updateField(@Param("id") Integer id, @Param("fieldName") String fieldName, @Param("fieldValue") Object fieldValue);

  @Select("${query}")
  List<ClientRecord> select(@Param("query") String query);
}
