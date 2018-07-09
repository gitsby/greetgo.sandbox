package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsCounter extends ClientRecordQueryMethods<Integer> {
  private ClientRecordFilter filter;

  private List params = new ArrayList();
  private SQL sql = new SQL();

  public ClientRecordsCounter(ClientRecordFilter filter) {
    super(filter, new SQL(), new ArrayList());
    this.filter = filter;
  }


  @Override
  void select() {
    sql.SELECT("count(*)");
  }

  @Override
  public Integer doInConnection(Connection connection) throws Exception {

    prepareSql();

    PreparedStatement statement = connection.prepareStatement(sql.toString());

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }

    ResultSet resultSet = statement.executeQuery();
    resultSet.next();
    int count = resultSet.getInt("count");

    resultSet.close();
    statement.close();
    return count;
  }

  @Override
  void join() {
  }

  @Override
  void leftJoin() {
  }

  @Override
  void from() {
    sql.FROM("client");
  }

  @Override
  void where() {
    sql.WHERE("client.actual=1");
  }

  @Override
  void orderBy() {
  }

}
