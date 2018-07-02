package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordRow;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordsReportView;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsRender extends ClientRecordQueryMethods<Void> {

  private ClientRecordsReportView view;

  private ClientRecordFilter filter;

  private List params = new ArrayList();
  private SQL sql = new SQL();

  public ClientRecordsRender(ClientRecordFilter filter, ClientRecordsReportView view) {
    super(filter);
    this.filter = filter;
    this.view = view;
    view.start();
    prepareSql();
  }

  @Override
  public void prepareSql() {
    super.prepareSql();
    where(sql, params);
  }

  @Override
  void select() {
    sql.SELECT("client.id," +
      " client.name," +
      " client.surname, " +
      " client.patronymic, " +
      " client.gender, " +
      " extract(year from age(birth_date)) as age," +
      " c2.name as charm, " +
      " accountMoneys.min as minBalance, " +
      " accountMoneys.max as maxBalance, " +
      " accountMoneys.sum as accBalance," +
      " client.actual ");
    sql.SELECT("client.actual");
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
      "        from client\n" +
      "          join client_account a on client.id = a.client_id\n" +
      "        group by client_id) as accountMoneys on client.id= accountMoneys.client_id");
  }

  @Override
  void from() {
    sql.FROM("client");
  }

  @Override
  void orderBy() {
    switch (filter.columnName) {
      case "surname":
        sql.ORDER_BY("surname asc ,\n" +
          "  name asc ,\n" +
          "  patronymic asc ");
        break;
      case "age":
        sql.ORDER_BY(" age ASC ");
        break;
      case "total":
        sql.ORDER_BY(" sum ASC ");
        break;
      case "max":
        sql.ORDER_BY(" max ASC ");
        break;
      case "min":
        sql.ORDER_BY(" min ASC ");
        break;
      case "-surname":
        sql.ORDER_BY(" surname desc ,\n" +
          "  name desc ,\n" +
          "  patronymic desc ");
        break;
      case "-age":
        sql.ORDER_BY(" age DESC ");
        break;
      case "-total":
        sql.ORDER_BY(" sum DESC ");
        break;
      case "-max":
        sql.ORDER_BY(" max DESC ");
        break;
      case "-min":
        sql.ORDER_BY(" min DESC ");
        break;
    }
  }

  @Override
  void limit() {

  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    PreparedStatement statement = connection.prepareStatement(sql.toString());

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }
    try (ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        ClientRecordRow clientRecord = new ClientRecordRow();
        clientRecord.id = resultSet.getInt("id");
        clientRecord.surname = resultSet.getString("surname");
        clientRecord.name = resultSet.getString("name");
        clientRecord.patronymic = (resultSet.getString("patronymic") != null) ? resultSet.getString("patronymic") : "";
        clientRecord.charm = resultSet.getString("charm");

        clientRecord.age = resultSet.getInt("age");
        clientRecord.maxBalance = resultSet.getDouble("maxBalance");
        clientRecord.minBalance = resultSet.getDouble("minBalance");
        clientRecord.accBalance = resultSet.getDouble("accBalance");

        view.appendRow(clientRecord);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
