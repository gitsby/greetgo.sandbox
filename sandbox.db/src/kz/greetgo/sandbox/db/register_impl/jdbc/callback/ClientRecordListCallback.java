package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import org.fest.util.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClientRecordListCallback extends ClientRecordSelectCallback<List<ClientRecord>> {

  public ClientRecordListCallback(ClientFilter filter) {
    this.filter = filter;
  }

  @Override
  public void offsetAndLimit() {
    if (filter.offset != null && filter.limit != null) {
      sql.append("LIMIT greatest(0,?) OFFSET greatest(0,?)");
      params.add(filter.limit);
      params.add(filter.offset);
    }
  }

  @Override
  public void returning() {

  }

  @Override
  public List<ClientRecord> run(PreparedStatement ps) throws SQLException {
    List<ClientRecord> clientRecords = Lists.newArrayList();
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        assert clientRecords != null;
        clientRecords.add(fromRs(rs));
      }
    }
    return clientRecords;
  }
}
