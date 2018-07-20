package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ClientRecordSelectCallback<ConnectionReturnType> extends ClientFilterCallback<ConnectionReturnType, ClientRecord> {
  @Override
  public void select() {
    sql.append("SELECT client.id, client.surname, client.name, client.patronymic, " +
      "date_part('year',age(client.birth_date)) AS age, " +
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
    sql.append("LEFT JOIN client_account ON client_account.client=client.id AND client_account.actual=1 AND client.actual=1 ");
  }

  @Override
  public void groupBy() {
    sql.append("GROUP BY client.id ");
  }

  @Override
  public void orderBy() {
    String direct = getSortDirection(filter);

    if (filter.sortByEnum != null)
      switch (filter.sortByEnum) {
        case FULL_NAME:
          sql.append(String.format("ORDER BY client.surname %s, client.name %s, client.patronymic %s ", direct, direct, direct));
          return;
        case AGE:
          sql.append(String.format("ORDER BY age %s ", direct));
          return;
        case MIDDLE_BALANCE:
          sql.append(String.format("ORDER BY middle_balance %s ", direct));
          return;
        case MAX_BALANCE:
          sql.append(String.format("ORDER BY max_balance %s ", direct));
          return;
        case MIN_BALANCE:
          sql.append(String.format("ORDER BY min_balance %s ", direct));
      }
  }

  private String getSortDirection(ClientFilter clientFilter) {
    if (clientFilter.sortDirection != null) return clientFilter.sortDirection.toString();
    return "";
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
