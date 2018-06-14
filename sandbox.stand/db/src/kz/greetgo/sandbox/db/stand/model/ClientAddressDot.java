package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.ClientAddress;

public class ClientAddressDot {
  public Integer client;
  public AddressTypeEnum type;
  public String street;
  public String house;
  public String flat;

  public ClientAddressDot() {}

  public ClientAddressDot(ClientAddress clientAddress) {
    client = clientAddress.client;
    type = clientAddress.type;
    street = clientAddress.street;
    house = clientAddress.house;
    flat = clientAddress.flat;
  }

  public ClientAddress toClientAddress() {
    ClientAddress clientAddress = new ClientAddress();
    clientAddress.client = client;
    clientAddress.type = type;
    clientAddress.street = street;
    clientAddress.house = house;
    clientAddress.flat = flat;
    return clientAddress;
  }
}
