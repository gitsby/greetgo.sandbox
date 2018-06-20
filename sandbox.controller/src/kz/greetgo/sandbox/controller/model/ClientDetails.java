package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class ClientDetails {
  public Integer id;
  public String surname;
  public String name;
  public String patronymic;
  public GenderEnum gender;
  public Date birthDate;
  public Integer charmId;
  public ClientAddress addressFact;
  public ClientAddress addressReg;
  public ClientPhone homePhone;
  public ClientPhone workPhone;
  public ClientPhone mobilePhone;
}
