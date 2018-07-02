package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsQuery extends ClientRecordQueryMethods<List<ClientRecord>> {

  private ClientRecordFilter filter;

  public ArrayList<Object> params = new ArrayList();
  public SQL sql = new SQL();

  public ClientRecordsQuery(ClientRecordFilter filter) {
    super(filter);
    this.filter = filter;
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();

    List<ClientRecord> clientRecords = new ArrayList<>();
    System.out.println("LAST: " + sql.toString());
    PreparedStatement statement = connection.prepareStatement(sql.toString());

    Thread.sleep(1000);

    for (int i = 0; i < params.size(); i++) {
      System.out.println("Param: " + params.get(i));
      statement.setObject(i + 1, params.get(i));
    }
    try (ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id = resultSet.getInt("id");

        System.out.println(clientRecord.id);
        clientRecord.surname = resultSet.getString("surname");
        clientRecord.name = resultSet.getString("name");
        clientRecord.patronymic = (resultSet.getString("patronymic") != null) ? resultSet.getString("patronymic") : "";
        clientRecord.charm = resultSet.getString("charm");
        clientRecord.age = resultSet.getInt("age");

        clientRecord.maxBalance = resultSet.getDouble("maxBalance");
        clientRecord.minBalance = resultSet.getDouble("minBalance");
        clientRecord.accBalance = resultSet.getDouble("accBalance");
        clientRecords.add(clientRecord);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    statement.close();
    return clientRecords;
  }

  @Override
  public void prepareSql() {
    super.prepareSql();
    where(sql, params);
  }

  @Override
  void select() {
    sql.SELECT(
      "client.id,\n" +
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
        sql.ORDER_BY(" surname asc ,\n" +
          "  name asc ,\n" +
          "  patronymic asc LIMIT ? OFFSET ?");
        break;
      case "age":
        sql.ORDER_BY(" age ASC LIMIT ? OFFSET ?");
        break;
      case "total":
        sql.ORDER_BY(" sum ASC LIMIT ? OFFSET ?");
        break;
      case "max":
        sql.ORDER_BY(" max ASC LIMIT ? OFFSET ?");
        break;
      case "min":
        sql.ORDER_BY(" min ASC LIMIT ? OFFSET ?");
        break;
      case "-surname":
        sql.ORDER_BY(" surname desc ,\n" +
          "  name desc ,\n" +
          "  patronymic desc LIMIT ? OFFSET ?");
        break;
      case "-age":
        sql.ORDER_BY(" age DESC LIMIT ? OFFSET ?");
        break;
      case "-total":
        sql.ORDER_BY(" sum DESC LIMIT ? OFFSET ?");
        break;
      case "-max":
        sql.ORDER_BY(" max DESC LIMIT ? OFFSET ?");
        break;
      case "-min":
        sql.ORDER_BY(" min DESC LIMIT ? OFFSET ?");
        break;
      default:
        sql.ORDER_BY("client.actual LIMIT ? OFFSET ?");
        break;
    }
  }

  @Override
  void limit() {
    params.add(Math.abs(filter.sliceNum * filter.paginationPage + filter.sliceNum));
    params.add(Math.abs(filter.sliceNum * filter.paginationPage));
  }
}
