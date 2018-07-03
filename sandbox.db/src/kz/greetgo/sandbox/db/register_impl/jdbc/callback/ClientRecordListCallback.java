package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.fest.util.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClientRecordListCallback extends ClientRecordSelectCallback<List<ClientRecord>, ClientRecord> {

  public ClientRecordListCallback(ClientFilter filter) {
    this.filter = filter;
  }

  @Override
  public void from() {
    sql.append("FROM client ");
  }

  @Override
  public void join() {
    sql.append("LEFT JOIN client_account ON client_account.client=client.id AND client_account.actual=1 ");
  }

  @Override
  public void update() {

  }

  @Override
  public void set() {

  }

  @Override
  public void insert() {

  }

  @Override
  public void values() {

  }

  @Override
  public void groupBy() {
    sql.append("GROUP BY client.id ");
  }

  @Override
  public void orderBy() {
    String direct = getSortDirection(filter);

    if (filter.sortByEnum != null)
      switch (filter.sortByEnum) {
        case FULL_NAME:
          sql.append(String.format("ORDER BY client.surname %s, client.name %s, client.patronymic %s ", direct, direct, direct));
          return;
        case AGE:
          sql.append(String.format("ORDER BY age %s ", direct));
          return;
        case MIDDLE_BALANCE:
          sql.append(String.format("ORDER BY middle_balance %s ", direct));
          return;
        case MAX_BALANCE:
          sql.append(String.format("ORDER BY max_balance %s ", direct));
          return;
        case MIN_BALANCE:
          sql.append(String.format("ORDER BY min_balance %s ", direct));
      }
  }

  private String getSortDirection(ClientFilter clientFilter) {
    if (clientFilter.sortDirection != null) return clientFilter.sortDirection.toString();
    return "";
  }

  @Override
  public void offsetAndLimit() {
    if (filter.offset != null && filter.limit != null) {
      sql.append("LIMIT ? OFFSET ?");
      params.add(filter.limit);
      params.add(filter.offset);
    }
  }

  @Override
  public void returning() {

  }

  @Override
  public ClientRecord fromRs(ResultSet rs) throws SQLException {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = rs.getInt("id");
    clientRecord.surname = rs.getString("surname");
    clientRecord.name = rs.getString("name");
    clientRecord.patronymic = rs.getString("patronymic");
    clientRecord.age = rs.getInt("age");
    clientRecord.middle_balance = rs.getFloat("middle_balance");
    clientRecord.max_balance = rs.getFloat("max_balance");
    clientRecord.min_balance = rs.getFloat("min_balance");
    return clientRecord;
  }

  @Override
  public List<ClientRecord> run(PreparedStatement ps) throws SQLException {
    List<ClientRecord> clientRecords = Lists.newArrayList();
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) clientRecords.add(fromRs(rs));
    }
    return clientRecords;
  }
}
