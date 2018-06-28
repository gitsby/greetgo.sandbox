package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsCounter extends ClientRecordQueryMethods<Integer> {
  public ClientRecordFilter filter;

  List params = new ArrayList();
  SQL sql = new SQL();

  public ClientRecordsCounter(ClientRecordFilter filter) {
    this.filter = filter;
    all();
  }

  @Override
  public void all() {
    sql.SELECT("count(*)");
    from();
    where();
  }

  @Override
  public Integer doInConnection(Connection connection) throws Exception {

    PreparedStatement statement = connection.prepareStatement(sql.toString());

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }

    try (ResultSet resultSet = statement.executeQuery()) {
      resultSet.next();
      return resultSet.getInt("count");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  void join() {

  }

  @Override
  void leftJoin() {

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
  void from() {
    sql.FROM("client");
  }

  @Override
  void orderBy() {

  }

  @Override
  void limit() {

  }
}
