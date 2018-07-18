package kz.greetgo.sandbox.db.migration.reader.objects;

public class AddressFromMigration {

  public String client_id;

  public String street;
  public String house;
  public String flat;

  public String type;

  public String getInsertString() {
    return "insert into temp_address(client_id,street, house, flat, type) values(?,?,?,?,?) on conflict (client_id, type)\n" +
      "  do update\n" +
      "    set street = case when EXCLUDED.street notnull and EXCLUDED.street != ''\n" +
      "      then EXCLUDED.street end,\n" +
      "      flat = case when EXCLUDED.flat notnull and EXCLUDED.flat != ''\n" +
      "        then EXCLUDED.flat end,\n" +
      "      house = case when EXCLUDED.street notnull and EXCLUDED.house != ''\n" +
      "        then EXCLUDED.street end;";
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
