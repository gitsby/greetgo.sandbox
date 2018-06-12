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
  public Date birth_day;
  public int charmId;
  public int addressFactId;
  public int addressRegId;
  public int homePhoneId;
  public int workPhoneId;
  public int mobilePhoneId;
  public List<Integer> accountsId = new ArrayList<>();

}
