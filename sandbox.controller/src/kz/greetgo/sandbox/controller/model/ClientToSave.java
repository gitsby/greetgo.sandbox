package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class ClientToSave {
  public Integer id;
  public String surname;
  public String name;
  public String patronymic;
  public Gender gender;
  public Date birth_day;
  public int charmId;
  public ClientAddress addressFact;
  public ClientAddress addressReg;
  public ClientPhone homePhone;
  public ClientPhone workPhone;
  public ClientPhone mobilePhone;
}
