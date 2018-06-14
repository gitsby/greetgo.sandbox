package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.model.PhoneType;

public class ClientPhoneDot {
  public Integer client;
  public PhoneType type;
  public String number;

  public ClientPhoneDot() {}

  public ClientPhoneDot(ClientPhone saveClientPhone) {
    client = saveClientPhone.client;
    type = saveClientPhone.type;
    number = saveClientPhone.number;
  }

  public ClientPhone toClientPhone() {
    ClientPhone clientPhone = new ClientPhone();
    clientPhone.client = client;
    clientPhone.type = type;
    clientPhone.number = number;
    return clientPhone;
  }
}
