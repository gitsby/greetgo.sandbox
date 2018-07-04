package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordRow;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordsReportView;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ClientRecordsRender extends ClientRecordQueryMethods<Void> {
  // FIXME: 7/4/18 Код для отчета и рекордов общий во многих местах, кроме лимита. Сделай ОБЩИМ!
  private ClientRecordsReportView view;

  private ClientRecordFilter filter;

  public ClientRecordsRender(ClientRecordFilter filter, ClientRecordsReportView view) {
    super(filter, new SQL(), new ArrayList());
    this.filter = filter;
    this.view = view;
    view.start();
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
    addSorting(filter, sql, false);
  }

  @Override
  void limit() {

  }

  @Override
  public Void doInConnection(Connection connection) throws Exception {
    prepareSql();
    where(sql, params);

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

    return null;
  }
}
