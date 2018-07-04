package kz.greetgo.sandbox.db.migration.reader.objects;

public class AddressFromMigration {

  public String street;
  public String house;
  public String flat;

  public String type;

  public String getInsertString() {
    return "insert into temp_address(street, house, flat, type) values(?,?,?,?);";
  }

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
