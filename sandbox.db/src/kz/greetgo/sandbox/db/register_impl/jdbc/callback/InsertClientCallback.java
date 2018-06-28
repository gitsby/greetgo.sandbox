package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

  }

  @Override
  public void insert() {
    sql.append("INSERT INTO client(surname, name, patronymic, gender, birth_date, charm_id) ");
  }

  @Override
  public void values() {
    sql.append("VALUES (?, ?, ?, ?, ?, ?) ");
    params.add(clientToSave.surname);
    params.add(clientToSave.name);
    params.add(clientToSave.patronymic);
    params.add(clientToSave.gender.name());
    // FIXME: 6/28/18 Попробуй без каста, должно работать дял PGSQL
    params.add(new java.sql.Date(clientToSave.birthDate.getTime()));
    params.add(clientToSave.charmId);
  }

  @Override
  public void set() {}

  @Override
  public void where() {}

  @Override
  public void groupBy() {}

  @Override
  public void orderBy() {}

  @Override
  public void offsetAndLimit() {}

  @Override
  public void returning() {
    sql.append("RETURNING id");
  }

  @Override
  public Integer fromRs(ResultSet rs) throws Exception {
    return rs.getInt("id");
  }

  @Override
  public Integer run(PreparedStatement ps) throws Exception {
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) return fromRs(rs);
    }
    return null;
  }
}
