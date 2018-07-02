package kz.greetgo.sandbox.db.migration.reader;

public class AddressFromMigration {

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
