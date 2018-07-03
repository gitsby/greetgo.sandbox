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

  public ClientRecordsQuery(ClientRecordFilter filter) {
    super(filter, new SQL(), new ArrayList());
    this.filter = filter;
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();
    where(sql, params);
    List<ClientRecord> clientRecords = new ArrayList<>();
    PreparedStatement statement = connection.prepareStatement(sql.toString());
    Thread.sleep(1000);

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
      ClientRecord clientRecord = new ClientRecord();
      clientRecord.id = resultSet.getInt("id");

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

    statement.close();
    return clientRecords;
  }

  @Override
  void select() {
    sql.SELECT(clientRecordsSelect);
  }

  @Override
  void join() {
    sql.JOIN(clientRecordsJoin);
  }

  @Override
  void leftJoin() {
    sql.LEFT_OUTER_JOIN(clientRecordsLeftOuterJoin);
  }

  @Override
  void from() {
    sql.FROM("client");
  }

  @Override
  void orderBy() {
    addSorting(filter, sql, true);
  }

  @Override
  void limit() {
    params.add(Math.abs(filter.sliceNum * filter.paginationPage + filter.sliceNum));
    params.add(Math.abs(filter.sliceNum * filter.paginationPage));
  }
}
