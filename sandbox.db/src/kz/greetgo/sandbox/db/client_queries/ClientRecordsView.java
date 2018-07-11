package kz.greetgo.sandbox.db.client_queries;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.util.List;

public class ClientRecordsView<T> extends ClientRecordQueryMethods<T> {


  ClientRecordsView(ClientRecordFilter filter, SQL sql, List<Object> params) {
    super(filter, sql, params);
  }

  @Override
  void select() {
    sql.SELECT("client.id,\n" +
      "  client.name,\n" +
      "  client.surname,\n" +
      "  client.patronymic,\n" +
      "  client.gender,\n" +
      "  extract(year from age(birth_date)) as age,\n" +
      "  c2.name                            as charm,\n" +
      "  accountMoneys.min                  as minBalance,\n" +
      "  accountMoneys.max                  as maxBalance,\n" +
      "  accountMoneys.sum                  as accBalance,\n" +
      "  client.actual");
  }

  @Override
  void join() {
    sql.JOIN("characters c2 on client.charm = c2.id");
  }

  @Override
  void leftJoin() {
    sql.LEFT_OUTER_JOIN("(select\n" +
      "          client_id,\n" +
      "          SUM(money),\n" +
      "          max(money),\n" +
      "          min(money)\n" +
      "        from client_account\n" +
      "        group by client_id) as accountMoneys on client.id=accountMoneys.client_id");
  }

  @Override
  void from() {
    sql.FROM("client");
  }

  @Override
  void where() {
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


  @Override
  void orderBy() {

  }


  void addSorting(boolean useLimit) {
    String limit = "";

    if (useLimit) {
      limit = "LIMIT ? OFFSET ?";
      params.add(filter.sliceNum);
      params.add(Math.abs(filter.sliceNum * filter.paginationPage));
    }

    switch (filter.columnName) {
      case "surname":
        sql.ORDER_BY("surname asc ,\n" +
          "  name asc ,\n" +
          "  patronymic asc " + limit);
        break;
      case "age":
        sql.ORDER_BY(" age ASC, id ASC " + limit);
        break;
      case "total":
        sql.ORDER_BY(" accBalance ASC " + limit);
        break;
      case "max":
        sql.ORDER_BY(" maxBalance ASC " + limit);
        break;
      case "min":
        sql.ORDER_BY(" minBalance ASC " + limit);
        break;
      case "-surname":
        sql.ORDER_BY(" surname desc ,\n" +
          "  name desc ,\n" +
          "  patronymic desc " + limit);
        break;
      case "-age":
        sql.ORDER_BY(" age DESC , id ASC " + limit);
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

  @Override
  public T doInConnection(Connection connection) throws Exception {
    return null;
  }
}
