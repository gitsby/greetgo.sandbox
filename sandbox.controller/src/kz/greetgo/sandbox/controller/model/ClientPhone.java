package kz.greetgo.sandbox.controller.model;

public class ClientPhone {
  public Integer id;
  public PhoneType type;
  public String number;

  public ClientPhone() {}

  public ClientPhone(Integer id, PhoneType type, String number) {
    this.id = id;
    this.type = type;
    this.number = number;
  }
}