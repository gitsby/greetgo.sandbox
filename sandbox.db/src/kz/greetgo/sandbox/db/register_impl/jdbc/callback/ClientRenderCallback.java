package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientRenderCallback extends ClientRecordSelectCallback<Void,ClientRow> {

  private final String name;
  private final ClientRender render;

  public ClientRenderCallback(String name, ClientFilter filter, ClientRender render) {
    this.name = name;
    this.filter = filter;
    this.render = render;
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
  public void update() {

  }

  @Override
  public void set() {

  }

  @Override
  public void insert() {

  }

  @Override
  public void values() {

  }

  @Override
  public void groupBy() {
    sql.append("GROUP BY client.id ");
  }

  @Override
  public void orderBy() {

  }

  @Override
  public void offsetAndLimit() {

  }

  @Override
  public void returning() {

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
