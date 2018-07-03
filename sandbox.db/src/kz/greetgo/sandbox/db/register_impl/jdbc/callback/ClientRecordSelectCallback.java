package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

public abstract class ClientRecordSelectCallback<ConnectionReturnType, RsReturnType> extends ClientFilterCallback<ConnectionReturnType, RsReturnType> {
  @Override
  public void select() {
    sql.append("SELECT client.id, client.surname, client.name, client.patronymic, date_part('year',age(client.birth_date)) AS age, " +
      "AVG(coalesce(client_account.money, 0.0)) AS middle_balance, " +
      "MAX(coalesce(client_account.money, 0.0)) AS max_balance, " +
      "MIN(coalesce(client_account.money, 0.0)) AS min_balance ");
  }
}
