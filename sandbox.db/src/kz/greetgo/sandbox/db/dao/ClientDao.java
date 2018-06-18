package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientDao {

  // ---------------------------------------
  @Insert("insert into client (   name,    surname,    patronymic,    gender, charm, birth_date) " +
    "                  values ( #{name}, #{surname}, #{patronymic}, #{gender},#{charm},#{birthDate})")
  void insertIntoClient(ClientToSave personDot);

  @Insert("insert into client_address (   clientid,    type,    street,    house, flat) " +
    "                  values ( #{clientId}, #{type}, #{street}, #{house},#{flat})")
  void insertIntoAddress(Address address);


  @Insert("insert into client_phone (   clientid,    number,    type) " +
    "                  values ( #{clientid}, #{number}, #{type})")
  void insertPhone(Phone phone);


  // ---------------------------------------

  @Select("select count(*) from client where concat(Lower(surname),Lower(name),Lower(patronymic)) like '%'||{searchName}||'%")
  int getClientCount(ClientRecordFilter filter);

  @Select("select id, name from characters")
  List<CharmRecord> getCharacters();

  @Select("select client.id, client.name, client.surname, client.patronymic, client.gender, client.birth_date, c2.name as charm\n" +
    "from client join characters c2 on client.charm = c2.id where client.id=#{id}")
  ClientDetails getClientById(int id);

  @Select("select " +
    "clientid, number, type from client_phone where clientid=#{id}")
  List<Phone> getPhonesWithClientId(int id);

  @Select("select clientid, type, street, house, flat from client_address where clientid=#{id}")
  List<Address> getAddressesWithClientId(int id);

  @Select("select\n" +
    "  client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  client.gender,\n" +
    "  extract(year from age(birth_date)) as age,\n" +
    "  c2.name as charm,\n" +
    "  accountMoneys.min as minBalance,\n" +
    "  accountMoneys.max as maxBalance,\n" +
    "  accountMoneys.sum as accBalance\n" +
    "from client\n" +
    "  join characters c2 on client.charm = c2.id\n" +
    "  left join (select\n" +
    "          clientid,\n" +
    "          SUM(money),\n" +
    "          max(money),\n" +
    "          min(money)\n" +
    "        from client\n" +
    "          join client_account a on client.id = a.clientid\n" +
    "        group by clientid) as accountMoneys on client.id= accountMoneys.clientid where client.id=#{id}")
  ClientRecord getClientRecordById(int id);

  @Select("select id from client order by id desc limit 1")
  int getLastInsertedClientId();

  // ---------------------------------------

  @Update("update client set name=#{name}, surname=#{surname}, patronymic=#{patronymic}," +
    " gender=#{gender}, birth_date=#{birthDate}, charm=#{charm} where id=#{id}")
  void updateClient(ClientToSave client);

  @Update("update client_phone set phone=#{editedTo} " +
    "where clientid=#{clientId} and phone=#{phone}")
  void updatePhone(Phone phone);

  @Update("update client_address set street=#{street}, house=#{house}, flat=#{flat} " +
    "where clientid=#{clientId} and type=#{type}")
  void updateAddress(Address address);


  // ---------------------------------------

  @Delete("delete from client where id=#{id}")
  void deleteClient(int id);

  @Delete("delete from client_address where clientid=#{clientId} and type=#{type}")
  void deleteAddress(Address address);

  @Delete("delete from client_phone where clientid=#{clientId} and number=#{number}")
  void deletePhone(Phone phone);

}
