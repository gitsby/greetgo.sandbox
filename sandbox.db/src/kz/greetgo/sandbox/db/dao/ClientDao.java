package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Character;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {
  @Select("select * from characters")
  List<Character> getCharacters();
}
