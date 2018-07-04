package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ClientRecordSelectCallback<ConnectionReturnType> extends ClientFilterCallback<ConnectionReturnType, ClientRecord> {
  @Override
  public void select() {
    sql.append("SELECT client.id, client.surname, client.name, client.patronymic, date_part('year',age(client.birth_date)) AS age, " +
      "AVG(coalesce(client_account.money, 0.0)) AS middle_balance, " +
      "MAX(coalesce(client_account.money, 0.0)) AS max_balance, " +
      "MIN(coalesce(client_account.money, 0.0)) AS min_balance ");
  }

  @Override
  public void from() {
    sql.append("FROM client ");
  }

  @Override
  public void join() {
    sql.append("LEFT JOIN client_account ON client_account.client=client.id AND client_account.actual=1 ");
  }

  @Override
  public void groupBy() {
    sql.append("GROUP BY client.id ");
  }

  @Override
  public ClientRecord fromRs(ResultSet rs) throws SQLException {
    ClientRecord row = new ClientRecord();
    row.id = rs.getInt("id");
    row.surname = rs.getString("surname");
    row.name = rs.getString("name");
    row.patronymic = rs.getString("patronymic");
    row.age = rs.getInt("age");
    row.middle_balance = rs.getFloat("middle_balance");
    row.max_balance = rs.getFloat("max_balance");
    row.min_balance = rs.getFloat("min_balance");
    return row;
  }
}
