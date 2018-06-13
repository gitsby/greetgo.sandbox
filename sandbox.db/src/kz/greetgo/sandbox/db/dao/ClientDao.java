package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {

  @Insert("insert into client (   name,    surname,    patronymic,    gender, charm, birth_date) " +
    "                  values ( #{name}, #{surname}, #{patronymic}, 'MALE',1,#{birthDate})")
  void insertIntoClient(ClientToSave personDot);

  @Insert("insert into client_address (   clientid,    type,    street,    house, flat) " +
    "                  values ( #{clientId}, #{type}, #{street}, #{house},#{flat})")
  void insertIntoAddress(Address address);


  @Insert("insert into client_phone (   clientid,    number,    type) " +
    "                  values ( #{client}, #{number}, #{type})")
  void insertPhones(Phone phone);

  @Select("select id, name from characters")
  List<Character> getCharacters();
}
