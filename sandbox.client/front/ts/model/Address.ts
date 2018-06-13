export class Address {
  public id: number;
  public clientId: number;
  public type: string = '';
  public house: string = '';
  public street: string = '';
  public flat: string = '';

  public static createNewAddress(address: Address): Address {
    let newAddress: Address = new Address();
    newAddress.id = address.id;
    newAddress.clientId = address.clientId;
    newAddress.type = address.type;
    newAddress.house = address.house;
    newAddress.street = address.street;
    newAddress.flat = address.flat;
    return newAddress;
  }
}
