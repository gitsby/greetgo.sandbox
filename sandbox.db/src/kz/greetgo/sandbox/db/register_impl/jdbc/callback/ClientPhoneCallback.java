package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientPhoneCallback extends SqlExecuteConnection<ClientPhone, ClientPhone> {

  private final Integer clientId;
  private final PhoneType type;

  public ClientPhoneCallback(Integer clientId, PhoneType type) {
    this.clientId = clientId;
    this.type = type;
  }

  @Override
  public void select() {
    sql.append("SELECT * ");
  }

  @Override
  public void from() {
    sql.append("FROM client_phone ");
  }

  @Override
  public void where() {
    sql.append("WHERE client=? AND type=? AND actual=1");
    params.add(clientId);
    params.add(type.name());
  }

  @Override
  public ClientPhone fromRs(ResultSet rs) throws SQLException {
    ClientPhone clientPhone = new ClientPhone();
    clientPhone.client = rs.getInt("client");
    clientPhone.number = rs.getString("number");
    clientPhone.type = PhoneType.valueOf(rs.getString("type"));
    return clientPhone;
  }

  @Override
  public ClientPhone run(PreparedStatement ps) throws SQLException {
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) return fromRs(rs);
    }
    return null;
  }
}
