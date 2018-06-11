package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.*;

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
  public ClientAddress addressFact;
  public ClientAddress addressReg;
  public ClientPhone homePhone;
  public ClientPhone workPhone;
  public ClientPhone mobilePhone;
  public List<ClientAccount> accounts = new ArrayList<>();

  public ClientRecords toClientRecords () {
    ClientRecords clientRecords = new ClientRecords();
    clientRecords.id = this.id;
    clientRecords.name = this.name;
    clientRecords.surname = this.surname;
    clientRecords.patronymic = this.patronymic;
    clientRecords.age = getAge();
    clientRecords.middle_balance = getMiddleBalance();
    clientRecords.max_balance = getMaxBalance();
    clientRecords.min_balance = getMinBalance();
    return clientRecords;
  }

  public ClientDetail toClientDetail() {
    ClientDetail clientDetail = new ClientDetail();
    clientDetail.id = this.id;
    clientDetail.name = this.name;
    clientDetail.surname = this.surname;
    clientDetail.patronymic = this.patronymic;
    clientDetail.birth_day = this.birth_day;
    clientDetail.charmId = this.charmId;
    clientDetail.addressFact = this.addressFact;
    clientDetail.addressReg = this.addressReg;
    clientDetail.homePhone = this.homePhone;
    clientDetail.mobilePhone = this.mobilePhone;
    clientDetail.workPhone = this.workPhone;
    clientDetail.gender = this.gender;
    return clientDetail;
  }

  private int getAge() {
    return (new Date()).getYear() - birth_day.getYear() + 1900;
  }

  private float getMiddleBalance() {
    if (accounts.size() == 0) return 0;
    float middle_balance = 0;
    for (int i = 0; i < accounts.size(); i++)
      middle_balance += accounts.get(i).money;
    return middle_balance/accounts.size();
  }

  private float getMaxBalance() {
    if (accounts.size() == 0) return 0;
    float max_balance = -1;
    for (int i = 0; i < accounts.size(); i++)
      if (accounts.get(i).money > max_balance) max_balance = accounts.get(i).money;
    return max_balance;
  }

  private float getMinBalance() {
    if (accounts.size() == 0) return 0;
    float min_balance = Integer.MAX_VALUE;
    for (int i = 0; i < accounts.size(); i++)
      if (accounts.get(i).money < min_balance) min_balance = accounts.get(i).money;
    return min_balance;
  }


}
