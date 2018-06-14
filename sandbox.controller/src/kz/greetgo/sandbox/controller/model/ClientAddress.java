package kz.greetgo.sandbox.controller.model;

public class ClientAddress {
  public Integer client;
  public AddressTypeEnum type;
  public String street;
  public String house;
  public String flat;

  public ClientAddress() {}

  public ClientAddress(Integer client, AddressTypeEnum type, String street, String house, String flat) {
    this.client = client;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}
