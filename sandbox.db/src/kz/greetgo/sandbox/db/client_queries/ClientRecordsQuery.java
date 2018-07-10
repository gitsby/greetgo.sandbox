package kz.greetgo.sandbox.db.client_queries;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientRecordsQuery extends ClientRecordsView<List<ClientRecord>> {

  public ClientRecordsQuery(ClientRecordFilter filter) {
    super(filter, new SQL(), new ArrayList<>());
  }

  @Override
  public List<ClientRecord> doInConnection(Connection connection) throws Exception {
    prepareSql();

    List<ClientRecord> clientRecords = new ArrayList<>();
    PreparedStatement statement = connection.prepareStatement(sql.toString());

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
    connection.close();
    return clientRecords;
  }


  @Override
  void orderBy() {
    addSorting(true);
  }

}
