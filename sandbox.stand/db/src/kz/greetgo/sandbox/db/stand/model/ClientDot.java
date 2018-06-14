package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.Gender;

import java.util.Date;

public class ClientDot {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public Gender gender;
<<<<<<< HEAD
  public Date birthDate;
  public Integer charmId;
=======
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
>>>>>>> 4b2de63b1530e50ea61a6c5ac81e6076fba915ec
}
