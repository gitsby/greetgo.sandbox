package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface ClientTestDao {

  @Insert("insert into characters (name) values(#{charm})")
  void insertNewCharacter(String charm);

  @Select("select id from client limit 1")
  Integer getFirstClient();

  @Select("select count(*) from client WHERE concat(Lower(name), Lower(surname), Lower(patronymic)) like '%'||#{searchName}||'%'")
  Integer getClientCount(ClientRecordFilter filter);

  @Select("select id from client limit 1")
  Integer getFirstCharacterId();

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
    "        group by clientid) as accountMoneys on client.id= accountMoneys.clientid" +
    " where client.id=#{id}")
  ClientRecord getClientRecordById(int id);

  @Select("select name from client where id=#{id}")
  String clientExists(int id);
}
