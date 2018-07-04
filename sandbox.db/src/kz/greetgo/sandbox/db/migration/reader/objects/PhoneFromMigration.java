package kz.greetgo.sandbox.db.migration.reader.objects;

public class PhoneFromMigration {

  public String client_id;
  public String number;
  public String type;

  public String getInsertString() {
    return "insert into temp_phone (client_id, number, type) values(?,?,?);";
  }
}
