package kz.greetgo.sandbox.db.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Bean
public interface ClientDao {

  @Select("SELECT id, surname, name, patronymic, gender, birth_date as birthDate, charm_id as charmId " +
    "FROM client " +
    "WHERE id=#{clientId} AND actual=1;")
  ClientDetails getDetails(@Param("clientId") Integer clientId);

  @Insert("INSERT INTO client_address(client, type, street, house, flat) " +
    "VALUES (#{clientId}, #{type}, #{street}, #{house}, #{flat}) " +
    "ON CONFLICT(client, type) " +
    "DO UPDATE SET " +
    "street=#{street}, house=#{house}, flat=#{flat};")
  void insertAddress(@Param("clientId") Integer clientId,
                     @Param("type") String type,
                     @Param("street") String street,
                     @Param("house") String house,
                     @Param("flat") String flat);

  @Insert("INSERT INTO client_phone(client, type, number) " +
    "VALUES (#{clientId}, #{type}, #{number}) " +
    "ON CONFLICT(client, type) " +
    "DO UPDATE SET number=#{number};")
  void insertPhone(@Param("clientId") Integer clientId, @Param("type") String type, @Param("number") String number);

  @Update("UPDATE client SET actual=0 WHERE id=#{clientId};")
  void setNotActual(@Param("clientId") Integer clientId);

  @Select("SELECT client, type, street, house, flat FROM client_address WHERE client=#{clientId} AND type=#{type} AND actual=1;")
  ClientAddress getClientAddress(@Param("clientId") Integer clientId, @Param("type") String type);

  @Select("SELECT id, name, description, energy FROM charm WHERE actual=1;")
  List<CharmRecord> getCharms();

  @Select("SELECT client.id, client.surname, client.name, client.patronymic, date_part('year',age(client.birth_date)) AS age," +
    "AVG(coalesce(client_account.money, 0.0)) AS middle_balance," +
    "MAX(coalesce(client_account.money, 0.0)) AS max_balance,"+
    "MIN(coalesce(client_account.money, 0.0)) AS min_balance FROM client LEFT JOIN client_account ON client.id=client_account.client WHERE client.id=#{clientId} AND client.actual=1 AND client_account.actual=1 GROUP BY client.id;")
  ClientRecord getClientRecord(@Param("clientId") Integer res);
}
