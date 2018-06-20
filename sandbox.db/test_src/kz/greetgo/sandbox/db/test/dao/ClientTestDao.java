package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Select;

public interface ClientTestDao {

  @Select("select id from client limit 1")
  int getFirstClient();
}
