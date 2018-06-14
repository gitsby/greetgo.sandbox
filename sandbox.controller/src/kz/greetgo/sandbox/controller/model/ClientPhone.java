package kz.greetgo.sandbox.controller.model;

public class ClientPhone {
  public Integer client;
  public PhoneType type;
  public String number;

  public ClientPhone() {}

  public ClientPhone(Integer client, PhoneType type, String number) {
    this.client = client;
    this.type = type;
    this.number = number;
  }
}