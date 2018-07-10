package kz.greetgo.sandbox.controller.controller;
//package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.TableToSend;
import kz.greetgo.sandbox.controller.model.User;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import static kz.greetgo.mvc.core.RequestMethod.POST;

// TODO: Все переменные и названия любых классох, директорий должны быть понятными !!!

@Bean
// TODO: маппинг сделать понятным. О каком table идёт речь и что он делает
@Mapping("/table")
// TODO: контроль соответственно назвать так, чтобы было понятно
public class TableController implements Controller {

  // TODO: переменную и класс регистра тоже переименую
  public BeanGetter<TableRegister> tableRegister;
  private String userID;

  @NoSecurity
  @ToJson
  @Mapping("/get-table-data")
  // TODO: я ведь показывал правильное наименование. Должен использоваться суффикс ...Record для данного случая.
  // Если забыл, подойди и спроси. Я покажу, не кусаюсь.
  public TableToSend getTableData(@Par("skipNumber") Integer skipNumber, @Par("limit") Integer limit,
                                  @Par("sortDirection") String sortDirection, @Par("sortType") String sortType,
                                  @Par("filterType") String filterType, @Par("filterText") String filterText) {
    return tableRegister.get().getTableData(skipNumber, limit, sortDirection, sortType, filterType, filterText);
  }


  @NoSecurity
  @ToJson
  @Mapping("/get-charms")
  // TODO: неверный вывод ответа для данного случая. Нельзя выдавать просто массив строк для характера.
  // TODO: и где реализация для RegisterImpl ?
  public String[] getCharms() {
    return tableRegister.get().getCharms();
  }


  @NoSecurity
  @ToJson
  @Mapping("/get-exact-user")
  // TODO: я ведь показывал правильное наименование. Должен использоваться суффикс ...Details для данного случая.
  // Если забыл, подойди и спроси. Я покажу, не кусаюсь.
  public User getExactUser(@Par("userID") Integer userID) {
    return tableRegister.get().getExactUser(userID);
  }


  @NoSecurity
  @ToJson
  @MethodFilter(POST)
  @Mapping("/create-user")
  // TODO: Почему статус ошибки возвращаешь в виде integer?
  // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
  // TODO: при добавлении надо возвращать UserRecord
  // поинтересуйся у меня, если возникут вопросы по этому поводу.
  public Integer createUser(@Par("user") @Json User user) {
    return tableRegister.get().createUser(user);
  }


  @NoSecurity
  @ToJson
  @MethodFilter(POST)
  @Mapping("/change-user")
  // TODO: Почему статусы возвращаешь в виде строки?
  // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
  // TODO: при редактирование надо возвращать UserRecord
  // поинтересуйся у меня, если возникут вопросы по этому поводу.
  public String changeUser(@Par("user") @Json User user) {
    return tableRegister.get().changeUser(user);
  }


  @NoSecurity
  @ToJson
  @MethodFilter(POST)
  @Mapping("/delete-user")
  // TODO: Почему статусы возвращаешь в виде строки?
  // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
  public String deleteUser(@Par("userID") Integer userID) {
    return tableRegister.get().deleteUser(userID);
  }
}
