package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.Gender;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

public interface ClientTestDao {

  @Insert("insert into Client (id, surname, name, patronymic, gender, birth_day, addressRegId, addressFactId, homePhoneId, workPhoneId, mobilePhoneId) " +
    "values (#{id}, #{surname}, #{name}, #{patronymic}, #{gender}, #{}, #{birth_day}, #{addressRegId}, #{addressFactId}, #{homePhoneId}, #{workPhoneId}, #{mobilePhoneId})")
  void insertClient(@Param("id") Integer id,
                    @Param("surname") String surname,
                    @Param("name") String name,
                    @Param("patronymic") String patronymic,
                    @Param("gender") Gender gender,
                    @Param("birth_day") Date birth_day,
                    @Param("addressRegId") Integer addressRegId,
                    @Param("addressFactId") Integer addressFactId,
                    @Param("homePhoneId") Integer homePhoneId,
                    @Param("workPhoneId") Integer workPhoneId,
                    @Param("mobilePhoneId") Integer mobilePhoneId);

  @Update("update Client set ${fieldName} = #{fieldValue} where id = #{id}")
  void updateClientField(@Param("id") Integer id,
                         @Param("fieldName") String fieldName,
                         @Param("fieldValue") Object fieldValue);


  //client_id, name, surname, patronymic, gender, birth_day, addressRegId, addressFactId, homePhoneId, workPhoneId, mobilePhoneId
}
