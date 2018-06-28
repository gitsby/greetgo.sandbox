package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientDao {

  // ---------------------------------------
  @Insert("insert into client_address (   client_id,    type,    street,    house, flat) " +
    "                  values ( #{clientId}, #{type}, #{street}, #{house},#{flat})")
  void insertAddress(Address address);


  @Insert("insert into client_phone (   client_id,    number,    type) " +
    "                  values ( #{client_id}, #{number}, #{type})")
  void insertPhone(Phone phone);


  // ---------------------------------------
  @Select("insert into client (   name,    surname,    patronymic,    gender, charm, birth_date, actual) " +
    "                  values ( #{name}, #{surname}, #{patronymic}, #{gender},#{charm},#{birthDate}, 1) returning id")
  int insertClient(ClientToSave personDot);

  // FIXME: 6/28/18 Doljni bit' tolko actuals
  @Select("select id, name from characters")
  List<CharmRecord> getCharms();

  @Select("select client.id, client.name, client.surname, client.patronymic, client.gender, client.birth_date as birthDate, client.charm\n" +
    "from client where client.id=#{id}")
  ClientDetails getClientById(int id);

  @Select("select " +
    "client_id, number, type from client_phone where client_id=#{id}")
  List<Phone> getPhonesWithClientId(int id);

  @Select("select client_id, type, street, house, flat from client_address where client_id=#{id}")
  List<Address> getAddressesWithClientId(int id);

  @Select("select id from client order by id desc limit 1")
  int getLastInsertedClientId();

  // ---------------------------------------

  @Update("update client set actual=0 where id=#{id}")
  void deleteClient(int id);

  @Update("update client set name=#{name}, surname=#{surname}, patronymic=#{patronymic}," +
    " gender=#{gender}, birth_date=#{birthDate}, charm=#{charm} where id=#{id}")
  void updateClient(ClientToSave client);

  @Update("update client_phone set phone=#{editedTo} " +
    "where client_id=#{clientId} and phone=#{phone}")
  void updatePhone(Phone phone);

  @Update("update client_address set street=#{street}, house=#{house}, flat=#{flat} " +
    "where client_id=#{clientId} and type=#{type}")
  void updateAddress(Address address);


  // ---------------------------------------
  @Delete("delete from client_address where client_id=#{clientId} and type=#{type}")
  void deleteAddress(Address address);

  @Delete("delete from client_phone where client_id=#{clientId} and number=#{number}")
  void deletePhone(Phone phone);

}
