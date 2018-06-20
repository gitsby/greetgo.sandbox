package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.GenderEnum;

import java.util.Date;

public class ClientDot {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public GenderEnum gender;
  public Date birthDate;
  public Integer charmId;
}
