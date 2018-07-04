package kz.greetgo.sandbox.db.migration.reader.objects;

public class NewAccountFromMigration {

  public String account_number;
  public String registered_at;
  public String type;
  public String client_id;

  @Override
  public String toString() {
    return "NewAccountFromMigration{" +
      "account_number='" + account_number + '\'' +
      ", registered_at='" + registered_at + '\'' +
      ", type='" + type + '\'' +
      ", client_id='" + client_id + '\'' +
      '}';
  }
}
