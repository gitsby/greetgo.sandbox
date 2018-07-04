package kz.greetgo.sandbox.db.client_records_query;


import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public abstract class ClientRecordQueryMethods<T> implements ConnectionCallback<T> {

  // FIXME: 7/4/18 Вынеси код, который не относится к каунту в нужное место

  public String clientRecordsSelect = "client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  client.gender,\n" +
    "  extract(year from age(birth_date)) as age,\n" +
    "  c2.name                            as charm,\n" +
    "  accountMoneys.min                  as minBalance,\n" +
    "  accountMoneys.max                  as maxBalance,\n" +
    "  accountMoneys.sum                  as accBalance,\n" +
    "  client.actual";

  public String clientRecordsLeftOuterJoin = "(select\n" +
    "          client_id,\n" +
    "          SUM(money),\n" +
    "          max(money),\n" +
    "          min(money)\n" +
    "        from client\n" +
    "          join client_account a on client.id = a.client_id\n" +
    "        group by client_id) as accountMoneys on client.id=accountMoneys.client_id";

  public String clientRecordsJoin = "characters c2 on client.charm = c2.id";

  public ClientRecordFilter filter;
  public SQL sql;
  public List params;

  public ClientRecordQueryMethods(ClientRecordFilter filter, SQL sql, List params) {
    this.filter = filter;
    this.sql = sql;
    this.params = params;
  }

  public void prepareSql() {
    select();
    from();
    join();
    leftJoin();
    orderBy();
    limit();
  }

  public void where(SQL sql, List params) {
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

  public void addSorting(ClientRecordFilter filter, SQL sql, boolean useLimit) {
    // FIXME: 7/4/18 фильтр уже есть в класса, зачем ты его еще раз передаешь?
    String limit = "";
    if (useLimit) {
      limit = "LIMIT ? OFFSET ?";
    }
    switch (filter.columnName) {
      case "surname":
        sql.ORDER_BY("surname asc ,\n" +
          "  name asc ,\n" +
          "  patronymic asc " + limit);
        break;
      case "age":
        sql.ORDER_BY(" age ASC " + limit);
        break;
      case "total":
        sql.ORDER_BY(" sum ASC " + limit);
        break;
      case "max":
        sql.ORDER_BY(" max ASC " + limit);
        break;
      case "min":
        sql.ORDER_BY(" min ASC " + limit);
        break;
      case "-surname":
        sql.ORDER_BY(" surname desc ,\n" +
          "  name desc ,\n" +
          "  patronymic desc " + limit);
        break;
      case "-age":
        sql.ORDER_BY(" age DESC " + limit);
        break;
      case "-total":
        sql.ORDER_BY(" sum DESC " + limit);
        break;
      case "-max":
        sql.ORDER_BY(" max DESC " + limit);
        break;
      case "-min":
        sql.ORDER_BY(" min DESC " + limit);
        break;
      default:
        sql.ORDER_BY("client.actual " + limit);
    }
  }

  abstract void select();

  abstract void join();

  abstract void leftJoin();

  abstract void from();

  abstract void orderBy();

  abstract void limit();
}
