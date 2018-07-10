package kz.greetgo.sandbox.db.client_queries;

import kz.greetgo.db.ConnectionCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class DefaultSaveQuery implements ConnectionCallback<Void> {

  public StringBuilder sql;
  public List<Object> params;

  public DefaultSaveQuery(StringBuilder sql, List<Object> params){
    this.sql = sql;
    this.params = params;
  }

  void prepareSql(){}

  @Override
  public Void doInConnection(Connection connection) throws Exception {

    prepareSql();
    PreparedStatement statement = connection.prepareStatement(sql.toString());
    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }
    statement.execute();
    connection.close();
    return null;
  }
}
