package kz.greetgo.sandbox.db.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Bean
public interface ClientDao {

  @Select("SELECT id, surname, name, patronymic, gender, birth_date as birthDate, charm_id as charmId FROM client WHERE id=#{clientId}")
  ClientDetails getDetails(@Param("clientId") Integer clientId);

  @Select("SELECT * FROM client WHERE id=#{clientId} AND actual=1")
  Client get(@Param("clientId") Integer clientId);

  @Select("SELECT * FROM charm WHERE id=#{id}")
  CharmRecord getCharm(@Param("id") Integer charmId);

  @Select("INSERT INTO client(surname, name, patronymic, gender, birth_date, charm) "+
    "VALUES (#{surname}, #{name}, #{patronymic}, #{gender}, #{birth_date}, #{charm}) RETURNING id;")
  Integer insert(@Param("surname") String surname,
                 @Param("name") String name,
                 @Param("patronymic") String patronymic,
                 @Param("gender") GenderEnum gender,
                 @Param("birth_date") Date birthDate,
                 @Param("charm") Integer charm);

  @Update("UPDATE client SET name=#{name}, surname=#{surname}, patronymic=#{patronymic}, gender=#{gender}, birth_date=#{birth_date}, charm=#{charm} WHERE id=#{id}")
  void update(@Param("id") Integer clientId,
              @Param("surname") String surname,
              @Param("name") String name,
              @Param("patronymic") String patronymic,
              @Param("gender") GenderEnum gender,
              @Param("birth_date") Date birth_date,
              @Param("charm") Integer charm);

  @Update("UPDATE client SET ${fieldName}=#{fieldValue} WHERE id=#{id}")
  void updateField(@Param("id") Integer id, @Param("fieldName") String fieldName, @Param("fieldValue") Object fieldValue);

  @Select("${query}")
  List<ClientRecord> select(@Param("query") String query);
}
