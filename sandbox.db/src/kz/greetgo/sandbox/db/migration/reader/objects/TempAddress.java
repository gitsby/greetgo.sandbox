package kz.greetgo.sandbox.db.migration.reader.objects;

public class TempAddress {

  public String client_id;

  public String street;
  public String house;
  public String flat;

  public String type;

  @Override
  public String toString() {
    return "AddressFromMigration{" +
      "street='" + street + '\'' +
      ", house='" + house + '\'' +
      ", flat='" + flat + '\'' +
      ", type='" + type + '\'' +
      '}';
  }
}
