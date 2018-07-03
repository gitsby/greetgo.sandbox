package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientRecord;
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

  // ------------------------------------
  @Select("insert into client (name, surname, patronymic, gender, birth_date, charm, actual) " +
    "values (#{name},#{surname},#{patronymic},#{gender},#{birthDate},#{charm}, 1) returning id")
  int insertNewClient(ClientDot clientDot);

  @Select("insert into characters (name) values(#{charm}) RETURNING id;")
  int insertNewCharacter(String charm);

  @Select("select count(*) from client " +
    "WHERE concat(Lower(name), Lower(surname), Lower(patronymic)) like '%'||#{searchName}||'%'" +
    " and actual=1")
  Integer getClientCount(ClientRecordFilter filter);

  @Select("select\n" +
    "  client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  client.gender,\n" +
    "  extract(year from age(birth_date)) as age,\n" +
    "  c2.name                            as charm,\n" +
    "  accountMoneys.min                  as minBalance,\n" +
    "  accountMoneys.max                  as maxBalance,\n" +
    "  accountMoneys.sum                  as accBalance,\n" +
    "  client.actual\n" +
    "from client\n" +
    "  join characters c2 on client.charm = c2.id\n" +
    "  left join (select\n" +
    "               client_id,\n" +
    "               SUM(money),\n" +
    "               max(money),\n" +
    "               min(money)\n" +
    "             from client\n" +
    "               join client_account a on client.id = a.client_id\n" +
    "             group by client_id) as accountMoneys on client.id = accountMoneys.client_id\n" +
    "where client.actual = 1 and client.id=#{id}")
  ClientRecord getClientRecordById(int id);

  @Select("select * from client " +
    "where " +
    "actual=1")
  List<ClientDot> getClientDotsWithFIO(String fio);

  @Select("select * from client where id =#{id} and actual=1")
  ClientDot getClientDotById(int id);

  @Select("select * from client_address where client_id=#{client_id}")
  AddressDot getAddressDot(int client_id);

  @Select("select * from client_address where client_id=#{client_id}")
  List<AddressDot> getAddressDots(int clientId);

  @Select("select client_id, number, type from client_phone where client_id=#{id}")
  PhoneDot getPhoneDot(int id);

  @Select("select client_id, number, type from client_phone where client_id=#{id}")
  List<PhoneDot> getPhoneDots(int id);

  @Select("select * from client where id=?")
  ClientDot getClientDotWithId(int id);

  @Select("select * from client where charm=#{charmId} and actual=1")
  ClientDot getClientDotWithCharmId(int charmId);

  @Select("select name from client where id=#{id} and actual=1")
  String clientExists(int id);

  //----------------------------------------

  @Update("update client set actual = 0")
  void deleteAll();

  @Update("update characters set actual=0")
  void deleteAllCharms();
}
