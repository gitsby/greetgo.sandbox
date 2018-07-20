package kz.greetgo.sandbox.db.migration.reader.objects;

public class TempTransaction {
  public String finished_at;
  public String account_number;
  public String money;
  public String transaction_type;
  public String type;

  public String toJson() {
    return "{\"finished_at\":\"" + finished_at + "\",\"account_number\":\""
      + account_number + "\",\"money\":\"" + money + "\"," +
      "\"transaction_type\":\"" + transaction_type + "\",\"type\":\"transaction\"}";
  }

  @Override
  public String toString() {
    return "TransactionFromMigration{" +
      "finished_at='" + finished_at + '\'' +
      ", account_number='" + account_number + '\'' +
      ", money=" + money +
      ", transaction_type='" + transaction_type + '\'' +
      ", type='" + type + '\'' +
      '}';
  }

}
