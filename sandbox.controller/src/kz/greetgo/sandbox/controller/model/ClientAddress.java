package kz.greetgo.sandbox.controller.model;

public class ClientAddress {
  public Integer id;
  public AddressType type;
  public String street;
  public String house;
  public String flat;

  public ClientAddress() {}

  public ClientAddress(Integer id, AddressType type, String street, String house, String flat) {
    this.id = id;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}
