package kz.greetgo.sandbox.db.dao;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Bean
public interface ClientDao {

  /** SELECTS */

  @Select("SELECT * FROM client WHERE id=#{clientId} AND actual=1")
  Client get(@Param("clientId") Integer clientId);

  @Select("SELECT * FROM client_address WHERE client=#{clientId} AND type=#{type} AND actual=1")
  ClientAddress getAddress(@Param("clientId") Integer clientId, @Param("type") AddressTypeEnum type);

  @Select("SELECT * FROM client_phone WHERE client=#{clientId} AND type=#{type} AND actual=1")
  ClientPhone getPhone(@Param("clientId") Integer clientId, @Param("type")PhoneType type);

  @Select("SELECT * FROM charm WHERE id=#{id}")
  Charm getCharm(@Param("id") Integer charmId);



}
