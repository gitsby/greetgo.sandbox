package kz.greetgo.sandbox.db.client_queries;

import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.ClientToSave;

import java.util.ArrayList;

public class AddressSaveQuery extends DefaultSaveQuery {

  private ClientToSave client;

  public AddressSaveQuery(ClientToSave client) {
    super(new StringBuilder(), new ArrayList<>());
    this.client = client;
  }

  @Override
  void prepareSql() {
    if (client.addedAddresses != null) {
      for (Address address : client.addedAddresses) {
        sql.append("insert into client_address (street, house, flat, client_id, type) values(?, ?, ?, ?, ?);");
        addAddressParams(address);
      }
    }

    if (client.editedAddresses != null) {
      for (Address address : client.editedAddresses) {
        sql.append("update client_address set street=?, house=?,flat=? where client_id=? and type=?;");
        addAddressParams(address);
      }
    }

    if (client.deletedAddresses != null) {
      for (Address address : client.deletedAddresses) {
        sql.append("update client_address set actual=0 where client_id=? and type=?;");
        params.add(client.id);
        params.add(address.type);
      }
    }

  }

  private void addAddressParams(Address address) {
    params.add(address.street);
    params.add(address.house);
    params.add(address.flat);
    params.add(client.id);
    params.add(address.type);
  }
}
