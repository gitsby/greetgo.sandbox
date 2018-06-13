package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Gender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientDot {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
  // FIXME: 6/13/18 CamelCase
  public Date birth_day;
  public Integer charmId;

  // FIXME: 6/13/18 ссылка должна быть на клиент
  public Integer addressFactId;
  public Integer addressRegId;
  public Integer homePhoneId;
  public Integer workPhoneId;
  public Integer mobilePhoneId;

  // FIXME: 6/13/18 не доожен быть здесь
  public List<Integer> accountsId = new ArrayList<>();
}
