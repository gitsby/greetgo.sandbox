package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import org.fest.util.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class SqlExecuteConnection<ConnectionReturnType, RsReturnType> implements ConnectionCallback<ConnectionReturnType> {

  protected StringBuilder sql = new StringBuilder();
  protected List<Object> params = Lists.newArrayList();

  public void select() {}
  public void from() {}
  public void join() {}
  public void update() {}
  public void set() {}
  public void insert() {}
  public void values() {}
  public void where() {}
  public void groupBy() {}
  public void orderBy() {}
  public void offsetAndLimit() {}
  public void returning() {}
  public abstract RsReturnType fromRs(ResultSet rs) throws SQLException;
  public abstract ConnectionReturnType run(PreparedStatement ps) throws SQLException;

  private final void prepareSql() {
    select();
    from();
    join();
    update();
    insert();
    values();
    set();
    where();
    groupBy();
    orderBy();
    offsetAndLimit();
    returning();
  }

  private void putParams(PreparedStatement ps) throws SQLException {
    for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
  }

  @Override
  public final ConnectionReturnType doInConnection(Connection connection) {
    prepareSql();
    PreparedStatement ps;
    try {
      ps = connection.prepareStatement(sql.toString());
      putParams(ps);
      return run(ps);
    } catch (SQLException e) {
      System.out.println(e.getNextException());
    }
    return null;
  }
}