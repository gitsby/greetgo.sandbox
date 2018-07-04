package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertClientCallback extends SqlExecuteConnection<Integer, Integer> {

  private ClientToSave clientToSave;

  public InsertClientCallback(ClientToSave clientToSave) {
    this.clientToSave = clientToSave;
  }

  @Override
  public void select() {

  }

  @Override
  public void from() {

  }

  @Override
  public void join() {

  }

  @Override
  public void update() {
    if (isExist(clientToSave)) return;
    sql.append("UPDATE client ");
  }

  @Override
  public void insert() {
    if (!isExist(clientToSave)) return;
    sql.append("INSERT INTO client(surname, name, patronymic, gender, birth_date, charm_id) ");
  }

  @Override
  public void values() {
    if (!isExist(clientToSave)) return;
    sql.append("VALUES (?, ?, ?, ?, ?, ?) ");
    params.add(clientToSave.surname);
    params.add(clientToSave.name);
    params.add(clientToSave.patronymic);
    params.add(clientToSave.gender.name());
    params.add(new java.sql.Date(clientToSave.birthDate.getTime()));
    params.add(clientToSave.charmId);
  }

  @Override
  public void set() {
    if (isExist(clientToSave)) return;
    sql.append("SET surname=?, name=?, patronymic=?, gender=?, birth_date=?, charm_id=? ");
    params.add(clientToSave.surname);
    params.add(clientToSave.name);
    params.add(clientToSave.patronymic);
    params.add(clientToSave.gender.name());
    params.add(new java.sql.Date(clientToSave.birthDate.getTime()));
    params.add(clientToSave.charmId);
  }

  @Override
  public void where() {
    if (isExist(clientToSave)) return;
    sql.append("WHERE id=? ");
    params.add(clientToSave.id);
  }

  @Override
  public void groupBy() {

  }

  @Override
  public void orderBy() {

  }

  @Override
  public void offsetAndLimit() {

  }

  @Override
  public void returning() {
    sql.append("RETURNING id");
  }

  private boolean isExist(ClientToSave clientToSave) {
    return clientToSave.id == null;
  }

  @Override
  public Integer fromRs(ResultSet rs) throws SQLException {
    return rs.getInt("id");
  }

  @Override
  public Integer run(PreparedStatement ps) throws SQLException {
    Integer res = null;
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) res = fromRs(rs);
    }
    return res;
  }
}
