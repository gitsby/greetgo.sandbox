package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.model.PhoneType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

public interface ClientTestDao {

  @Insert("insert into Client (client, surname, name, patronymic, gender, birthDate, addressRegId, addressFactId, homePhoneId, workPhoneId, mobilePhoneId) " +
    "values (#{client}, #{surname}, #{name}, #{patronymic}, #{gender}, #{birthDate}, #{addressRegId}, #{addressFactId}, #{homePhoneId}, #{workPhoneId}, #{mobilePhoneId})")
  void insertClient(@Param("client") Integer id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birthDate") Date birth_day,
                    @Param("addressRegId") Integer addressRegId,
                    @Param("addressFactId") Integer addressFactId,
                    @Param("homePhoneId") Integer homePhoneId,
                    @Param("workPhoneId") Integer workPhoneId,
                    @Param("mobilePhoneId") Integer mobilePhoneId);


  @Insert("insert into ClientPhone (client, type, number) " +
    "values (#{clientId}, #{type}, #{number})")
  void insertClientPhone(@Param("clientId") Integer clientId,
                         @Param("type") PhoneType type,
                         @Param("number") String number);

  @Insert("insert into ClientAddress (client, type, street, house, flat) "+
    "values (#{clientId}, #{type}, #{street}, #{house}, #{flat})")
  void insertClientAddress(@Param("clientId") Integer clientId,
                           @Param("type") AddressTypeEnum type,
                           @Param("street") String street,
                           @Param("house") String house,
                           @Param("flat") String flat);

  @Update("update Client set ${fieldName} = #{fieldValue} where client = #{client}")
  void updateClientField(@Param("client") Integer id,
                         @Param("fieldName") String fieldName,
                         @Param("fieldValue") Object fieldValue);

}
