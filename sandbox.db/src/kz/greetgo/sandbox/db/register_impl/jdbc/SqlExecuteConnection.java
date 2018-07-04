package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import org.apache.log4j.Logger;
import org.fest.util.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class SqlExecuteConnection<ConnectionReturnType, RsReturnType> implements ConnectionCallback<ConnectionReturnType> {

  private Logger logger = Logger.getLogger("callbacks_log");

  protected StringBuilder sql = new StringBuilder();
  protected List<Object> params = Lists.newArrayList();

  public abstract void select();
  public abstract void from();
  public abstract void join();
  public abstract void update();
  public abstract void set();
  public abstract void insert();
  public abstract void values();
  public abstract void where();
  public abstract void groupBy();
  public abstract void orderBy();
  public abstract void offsetAndLimit();
  public abstract void returning();
  public abstract RsReturnType fromRs(ResultSet rs) throws SQLException;
  public abstract ConnectionReturnType run(PreparedStatement ps) throws SQLException;

  // FIXME: 7/4/18 Если метод приватный, то нет смысла делать его финальным
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
    ConnectionReturnType res = null;
    try(PreparedStatement ps = connection.prepareStatement(sql.toString())) {
      putParams(ps);
      res = run(ps);
    } catch (SQLException e) {
      // FIXME: 7/4/18 catch ne nujen. throws Exception
      logger.error(e);
    }
    return res;
  }
}