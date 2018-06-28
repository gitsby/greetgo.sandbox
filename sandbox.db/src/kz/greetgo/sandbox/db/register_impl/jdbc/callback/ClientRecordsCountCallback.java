package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientRecordsCountCallback extends SqlExecuteConnection<Integer, Integer> {

  private ClientFilter filter;

  public ClientRecordsCountCallback(ClientFilter filter) {
    this.filter = filter;
  }

  @Override
  public void select() {
    sql.append("SELECT COUNT(*) AS result ");
  }

  @Override
  public void from() {
    sql.append("FROM client ");
  }

  @Override
  public void join() {

  }

  @Override
  public void update() {

  }

  @Override
  public void insert() {

  }

  @Override
  public void values() {

  }

  @Override
  public void set() {

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
  public Integer fromRs(ResultSet rs) throws Exception {
    return rs.getInt("result");
  }

  @Override
  public Integer run(PreparedStatement ps) throws Exception {
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) return fromRs(rs);
    }
    return null;
  }
}
