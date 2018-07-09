package kz.greetgo.sandbox.db.client_records_query;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import org.apache.ibatis.jdbc.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientSaveQuery implements ConnectionCallback<Integer> {

  private ClientToSave client;

  private SQL sql = new SQL();
  private List params = new ArrayList();

  public ClientSaveQuery(ClientToSave client) {
    this.client = client;

    if (client.id == null) {
      sql.INSERT_INTO("client");
      sql.INTO_COLUMNS("surname,name,patronymic,gender,birth_date,charm");
      sql.INTO_VALUES("?,?,?,?,?,?");
      params.add(client.surname);
      params.add(client.name);
      params.add(client.patronymic);
      params.add(client.gender);
      params.add(new java.sql.Date(client.birthDate.getTime()));
      params.add(client.charm);
    } else {
      sql.UPDATE("client");
      validateClientUpdate();
      sql.WHERE("id=?");
      params.add(client.id);
    }
  }

  private void validateClientUpdate() {
    if (client.name != null) {
      sql.SET(" name=? ");
      params.add(client.name);
    }
    if (client.surname != null) {
      sql.SET(" surname=? ");
      params.add(client.surname);
    }
    if (client.patronymic != null) {
      sql.SET(" patronymic=? ");
      params.add(client.patronymic);
    }
    if (client.birthDate != null) {
      sql.SET(" birth_date=? ");
      params.add(new java.sql.Date(client.birthDate.getTime()));
    }
    if (client.gender != null) {
      sql.SET("gender=?");
      params.add(client.gender);
    }
    if (client.charm != null) {
      sql.SET(" charm=? ");
      params.add(client.charm);
    }

  }

  @Override
  public Integer doInConnection(Connection connection) throws Exception {
    PreparedStatement statement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

    for (int i = 0; i < params.size(); i++) {
      statement.setObject(i + 1, params.get(i));
    }

    statement.execute();
    ResultSet set = statement.getGeneratedKeys();
    set.next();
    int clientId = set.getInt("id");

    connection.close();
    set.close();
    return clientId;
  }
}
