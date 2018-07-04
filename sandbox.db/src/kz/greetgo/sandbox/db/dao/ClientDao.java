package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
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
  @Select("select id, name from characters where actual=1")
  List<CharmRecord> getCharms();

  @Select("select client.id, client.name, client.surname, client.patronymic, client.gender, client.birth_date as birthDate, client.charm\n" +
    "from client where client.id=#{id} and actual=1")
  ClientDetails getClientById(int id);

  @Select("select " +
    "client_id, number, type from client_phone where client_id=#{id} and actual=1")
  List<Phone> getPhonesWithClientId(int id);

  @Select("select client_id, type, street, house, flat from client_address where client_id=#{id} and actual=1")
  List<Address> getAddressesWithClientId(int id);


  // ---------------------------------------

  @Update("update client set actual=0 where id=#{id}")
  void deleteClient(int id);

  @Update("update client_phone set number=#{editedTo} " +
    "where client_id=#{client_id} and number=#{number}")
  void updatePhone(Phone phone);

  @Update("update client_address set street=#{street}, house=#{house}, flat=#{flat} " +
    "where client_id=#{clientId} and type=#{type}")
  void updateAddress(Address address);


  @Update("update client_address set actual=0 where client_id=#{clientId} and type=#{type}")
  void deleteAddress(Address address);

  @Update("update client_phone set actual=0 where client_id=#{clientId} and number=#{number}")
  void deletePhone(Phone phone);

}
