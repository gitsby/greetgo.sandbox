package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.db.register_impl.jdbc.SqlExecuteConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientAddressCallback extends SqlExecuteConnection<ClientAddress, ClientAddress> {

  private final Integer clientId;
  private final AddressTypeEnum type;

  public ClientAddressCallback(Integer clientId, AddressTypeEnum type) {

    this.clientId = clientId;
    this.type = type;
  }

  @Override
  public void select() {
    sql.append("SELECT * ");
  }

  @Override
  public void from() {
    sql.append("FROM client_address ");
  }

  @Override
  public void join() {

  }

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
  public void where() {
    sql.append("WHERE client=? AND type=? AND actual=1");
    params.add(clientId);
    params.add(type.name());
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

  }

  @Override
  public ClientAddress fromRs(ResultSet rs) throws Exception {
    ClientAddress clientAddress = new ClientAddress();
    clientAddress.client = rs.getInt("client");
    clientAddress.type = AddressTypeEnum.valueOf(rs.getString("type"));
    clientAddress.street = rs.getString("street");
    clientAddress.house = rs.getString("house");
    clientAddress.flat = rs.getString("flat");
    return clientAddress;
  }

  @Override
  public ClientAddress run(PreparedStatement ps) throws Exception {
    try(ResultSet rs = ps.executeQuery()) {
      if (rs.next()) return fromRs(rs);
    }
    return null;
  }
}
