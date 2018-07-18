package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.Phone;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientDao {

  // ---------------------------------------
  @Select("select client_id, name from characters where actual=1")
  List<CharmRecord> getCharms();

  @Select("select client.client_id, client.name, client.surname, client.patronymic, client.gender, client.birth_date as birthDate, client.charm\n" +
    "from client where client.client_id=#{client_id} and actual=1")
  ClientDetails getClientById(int id);

  @Select("select " +
    "client_id, number, type from client_phone where client_id=#{client_id} and actual=1")
  List<Phone> getPhonesWithClientId(int id);

  @Select("select client_id, type, street, house, flat from client_address where client_id=#{client_id} and actual=1")
  List<Address> getAddressesWithClientId(int id);


  // ---------------------------------------

  @Update("update client set actual=0 where client_id=#{client_id}")
  void deleteClient(int id);
}
