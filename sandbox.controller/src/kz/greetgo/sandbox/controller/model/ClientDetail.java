package kz.greetgo.sandbox.controller.model;

import java.util.Date;

// FIXME: 6/13/18 Details
public class ClientDetail {
  public Integer id;
  public String surname;
  public String name;
  public String patronymic;
  public Gender gender;
  public Date birth_day;
  public Charm charm;
  public ClientAddress addressFact;
  public ClientAddress addressReg;
  public ClientPhone homePhone;
  public ClientPhone workPhone;
  public ClientPhone mobilePhone;

  public ClientDetail() {}

  public ClientDetail(Integer id, String surname, String name, String patronymic, Gender gender, Date birth_day, Charm charm, ClientAddress addressFact, ClientAddress addressReg, ClientPhone homePhone, ClientPhone workPhone, ClientPhone mobilePhone) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birth_day = birth_day;
    this.charm = charm;
    this.addressFact = addressFact;
    this.addressReg = addressReg;
    this.homePhone = homePhone;
    this.workPhone = workPhone;
    this.mobilePhone = mobilePhone;
  }
}
