package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.ClientInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {


  @Select("select count(*) from Client")
  int getClientsCount();

  @Select("select count(*) from Client where name like #{fio} or surname like #{fio} or patronymic like #{fio}")
  int getClientsCountWithFilter(@Param("fio") String fio);

  @Select("select * from Client where client = #{client}")
  ClientInfo get(@Param("client") Integer clientId);


  @Select("select * from Client")
  List<ClientInfo> getAll();

}
