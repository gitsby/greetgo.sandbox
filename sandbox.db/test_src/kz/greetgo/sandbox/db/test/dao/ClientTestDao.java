package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientAccount;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ClientTestDao {


  @Insert("insert into client_address (client_id, type, street, house, flat) values " +
    "(#{client_id}, #{type},#{street},#{house},#{flat})")
  void insertNewAddressDot(AddressDot addressDot);

  @Insert("insert into client_phone (client_id, number, type) " +
    "values(#{client_id},#{number},#{type})")
  void insertNewPhoneDot(PhoneDot phoneDot);

  @Insert("insert into client_account (client_id, money) values (#{id}, #{money})")
  void insertNewAccount(ClientAccount account);

  // ------------------------------------
  @Select("insert into client (name, surname, patronymic, gender, birth_date, charm, actual) " +
    "values (#{name},#{surname},#{patronymic},#{gender},#{birthDate},#{charm}, 1) returning id")
  int insertNewClient(ClientDot clientDot);

  @Select("insert into characters (name) values(#{charm}) RETURNING id;")
  int insertNewCharm(String charm);

  @Select("select count(*) from client " +
    "WHERE concat(Lower(name), Lower(surname), Lower(patronymic)) like '%'||#{searchName}||'%'" +
    " and actual=1")
  Integer getClientCount(ClientRecordFilter filter);


  @Select("select * from client where id =#{client_id} and actual=1")
  ClientDot getClientDotById(int id);

  @Select("select * from client_address where client_id=#{client_id} and actual=1")
  AddressDot getAddressDot(int client_id);

  @Select("select * from client_address where client_id=#{client_id} and actual=1 and type='REG'")
  AddressDot getRegAddress(int client_id);

  @Select("select * from client_address where client_id=#{client_id} and actual=1")
  List<AddressDot> getAddressDots(int clientId);

  @Select("select client_id, number, type from client_phone where client_id=#{client_id} and actual=1 and type='MOBILE'")
  PhoneDot getMobilePhone(int id);

  @Select("select client_id, number, type from client_phone where client_id=#{client_id} and actual=1")
  List<PhoneDot> getPhoneDots(int id);

  @Select("select name from client where id=#{client_id} and actual=1")
  String clientExists(int id);

  //----------------------------------------

  @Update("update client set actual = 0")
  void deleteAll();

  @Update("update characters set actual=0")
  void deleteAllCharms();
}
