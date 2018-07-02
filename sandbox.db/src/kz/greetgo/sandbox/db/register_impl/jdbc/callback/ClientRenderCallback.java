package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientRenderCallback extends SqlExecuteConnection<Void,ClientRow> {

  private final String name;
  private ClientFilter filter;
  private final ClientRender render;

  public ClientRenderCallback(String name, ClientFilter filter, ClientRender render) {
    this.name = name;
    this.filter = filter;
    this.render = render;
  }

  @Override
  public void select() {
    sql.append("SELECT client.id, client.surname, client.name, client.patronymic, date_part('year',age(client.birth_date)) AS age, " +
      "AVG(coalesce(client_account.money, 0)) AS middle_balance, " +
      "MAX(coalesce(client_account.money, 0)) AS max_balance, " +
      "MIN(coalesce(client_account.money, 0)) AS min_balance ");
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
  public void where() {
    sql.append("WHERE client.actual=1 ");
    if (filter.fio != null) {
      if (!filter.fio.isEmpty()) {
        sql.append("AND (client.name LIKE ? OR client.surname LIKE ? OR client.patronymic LIKE ?) ");
        params.add("%" + filter.fio + "%");
        params.add("%" + filter.fio + "%");
        params.add("%" + filter.fio + "%");
      }
    }
  }

  @Override
  public void groupBy() {
    sql.append("GROUP BY client.id ");
  }

  @Override
  public ClientRow fromRs(ResultSet rs) throws SQLException {
    ClientRow row = new ClientRow();
    row.id = rs.getInt("id");
    row.surname = rs.getString("surname");
    row.name = rs.getString("name");
    row.patronymic = rs.getString("patronymic");
    row.age = rs.getInt("age");
    row.middle_balance = rs.getInt("middle_balance");
    row.max_balance = rs.getInt("max_balance");
    row.min_balance = rs.getInt("min_balance");
    return row;
  }

  @Override
  public Void run(PreparedStatement ps) throws SQLException {
    try(ResultSet rs = ps.executeQuery()) {
      render.start(name, new Date());
      while (rs.next()) render.append(fromRs(rs));
      render.finish();
    }
    return null;
  }
}
