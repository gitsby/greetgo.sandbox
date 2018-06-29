package kz.greetgo.sandbox.db.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Bean
public interface ClientDao {

  //language=PostgreSQL
  @Select("SELECT id, surname, name, patronymic, gender, birth_date as birthDate, charm_id as charmId " +
    "FROM client " +
    "WHERE id= #{clientId} AND actual=1")
  ClientDetails getDetails(@Param("clientId") Integer clientId);

  //language=PostgreSQL
  @Insert("INSERT INTO client_address(client, type, street, house, flat) " +
    "VALUES (#{clientId}, #{type}, #{street}, #{house}, #{flat}) " +
    "ON CONFLICT(client, type) " +
    "DO UPDATE SET " +
    "street= #{street}, house= #{house}, flat= #{flat}")
  void insertAddress(@Param("clientId") Integer clientId,
                     @Param("type") String type,
                        @Param("street") String street,
                        @Param("house") String house,
                     @Param("flat") String flat);

  //language=PostgreSQL
  @Insert("INSERT INTO client_phone(client, type, number) " +
    "VALUES (#{clientId}, #{type}, #{number}) " +
    "ON CONFLICT(client, type) " +
    "DO UPDATE SET number= #{number}")
  void insertPhone(@Param("clientId") Integer clientId, @Param("type") String type, @Param("number") String number);

  //language=PostgreSQL
  @Update("UPDATE client " +
    "SET actual=0 " +
    "WHERE id= #{clientId}")
  void setNotActual(@Param("clientId") Integer clientId);
}
