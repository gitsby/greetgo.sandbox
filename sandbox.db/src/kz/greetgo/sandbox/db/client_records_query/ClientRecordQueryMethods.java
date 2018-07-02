package kz.greetgo.sandbox.db.client_records_query;


import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public abstract class ClientRecordQueryMethods<T> implements ConnectionCallback<T> {

  public ClientRecordFilter filter;

  public ClientRecordQueryMethods(ClientRecordFilter filter) {
    this.filter = filter;
  }

  public void prepareSql() {
    select();
    from();
    join();
    leftJoin();
    orderBy();
    limit();
  }

  void where(SQL sql, List params) {
    if (filter.searchName != null) {
      if (filter.searchName.length() != 0) {
        sql.WHERE(" concat(Lower(client.name), Lower(client.surname), Lower(client.patronymic)) like '%'||?||'%' ");
        params.add(filter.searchName);
      } else {
        filter.searchName = null;
      }
    }
    sql.WHERE("client.actual=1");
  }

  abstract void select();

  abstract void join();

  abstract void leftJoin();

  abstract void from();

  abstract void orderBy();

  abstract void limit();
}
