package kz.greetgo.sandbox.db.migration.reader;

public class PhoneFromMigration {

  public String client_id;
  public String number;
  public String type;

  public String toInsertString() {
    return "insert into temp_phone (client_id, number, type) values('" + client_id + "','" + number + "','" + type + "');";
  }

}
