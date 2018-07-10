package kz.greetgo.sandbox.db.client_queries;


import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public abstract class ClientRecordQueryMethods<T> implements ConnectionCallback<T> {

  ClientRecordFilter filter;
  public SQL sql;
  public List<Object> params;

  ClientRecordQueryMethods(ClientRecordFilter filter, SQL sql, List<Object> params) {
    this.filter = filter;
    this.sql = sql;
    this.params = params;
  }

  void prepareSql() {
    select();
    from();
    join();
    where();
    leftJoin();
    orderBy();
  }

  abstract void select();

  abstract void join();

  abstract void leftJoin();

  abstract void from();

  abstract void where();

  abstract void orderBy();

}
