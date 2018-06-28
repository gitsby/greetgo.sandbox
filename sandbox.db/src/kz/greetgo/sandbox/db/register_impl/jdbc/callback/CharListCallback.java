package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;
import org.fest.util.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class CharListCallback extends SqlExecuteConnection<List<CharmRecord>, CharmRecord> {

  @Override
  public void select() {
    sql.append("SELECT * ");
  }

  @Override
  public void from() {
    sql.append("FROM charm ");
  }

  @Override
  public void join() {}

  @Override
  public void update() {

  }

  @Override
  public void insert() {

  }

  @Override
  public void values() {

  }

  @Override
  public void set() {

  }

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

  }

  @Override
  public CharmRecord fromRs(ResultSet rs) throws Exception {
    CharmRecord charmRecord = new CharmRecord();
    charmRecord.id = rs.getInt("id");
    charmRecord.name = rs.getString("name");
    charmRecord.description = rs.getString("description");
    charmRecord.energy = rs.getFloat("energy");
    return charmRecord;
  }

  @Override
  public List<CharmRecord> run(PreparedStatement ps) throws Exception {
    List<CharmRecord> charmRecords = Lists.newArrayList();
    try(ResultSet rs = ps.executeQuery()) {
      while (rs.next()) charmRecords.add(fromRs(rs));
    }
    return charmRecords;
  }
}
