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

  @Override
  public boolean equals(Object obj) {
    ClientPhone clientPhone = (ClientPhone) obj;
    if (type.equals(clientPhone.type) &&
      number.equals(clientPhone.number)) return true;
    return false;
  }

  @Override
  public String toString() {
    return "ClientPhone{" +
      "client=" + client +
      ", type=" + type +
      ", number='" + number + '\'' +
      '}';
  }
}