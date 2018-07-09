package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.model.ClientRecordRow;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ClientRecordsRender extends ClientRecordsView<Void> {

  private ClientRecordsReportView view;

  public ClientRecordsRender(ClientRecordFilter filter, ClientRecordsReportView view) {
    super(filter, new SQL(), new ArrayList<>());
    this.view = view;
    view.start();
  }

  @Override
  void orderBy() {
    addSorting(false);
  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    prepareSql();

    PreparedStatement statement = connection.prepareStatement(sql.toString());

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }

    ResultSet resultSet = statement.executeQuery();

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
    resultSet.close();

    return null;
  }
}
