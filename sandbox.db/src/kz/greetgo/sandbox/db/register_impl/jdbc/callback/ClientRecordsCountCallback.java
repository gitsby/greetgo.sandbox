package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientRecordsCountCallback extends ClientFilterCallback<Integer, Integer> {


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

  // FIXME: 7/4/18 update, set и прочее определи в ClientFilterCallback
  @Override
  public void join() {}
  @Override
  public void update() {}
  @Override
  public void set() {}
  @Override
  public void insert() {}
  @Override
  public void values() {}
  @Override
  public void groupBy() {}
  @Override
  public void orderBy() {}
  @Override
  public void offsetAndLimit() {}
  @Override
  public void returning() {}

  @Override
  public Integer fromRs(ResultSet rs) throws SQLException {
    return rs.getInt("result");
  }

  @Override
  public Integer run(PreparedStatement ps) throws SQLException {
    Integer res = null;
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) res = fromRs(rs);
    }
    return res;
  }
}
