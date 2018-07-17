package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.client_queries.*;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {
    return jdbc.get().execute(new ClientRecordsQuery(clientRecordFilter));
  }

  @Override
  public void deleteClient(int clientId) {
    clientDao.get().deleteClient(clientId);
  }

  @Override
  public ClientDetails details(int clientId) {
    ClientDetails details = clientDao.get().getClientById(clientId);
    if (details == null) {
      return null;
    }
    details.phones = clientDao.get().getPhonesWithClientId(clientId);
    details.addresses = clientDao.get().getAddressesWithClientId(clientId);
    return details;
  }

  @Override
  public ClientRecord save(ClientToSave editedClient) {
    editedClient.id = jdbc.get().execute(new ClientSaveQuery(editedClient));

    jdbc.get().execute(new AddressSaveQuery(editedClient));

    jdbc.get().execute(new PhoneSaveQuery(editedClient));

    return getClientRecordById(editedClient.id);
  }

  private ClientRecord getClientRecordById(int clientId) {
    ClientRecordFilter filter = new ClientRecordFilter();
    filter.paginationPage = 0;
    filter.sliceNum = 1;
    filter.columnName = "empty";

    ClientRecordsQuery query = new ClientRecordsQuery(filter);
    query.sql.WHERE("client.client_id=?");

    query.params.add(clientId);
    return jdbc.get().execute(query).get(0);
  }

  @Override
  public List<CharmRecord> charm() {
    return clientDao.get().getCharms();
  }

  @Override
  public int getClientCount(ClientRecordFilter clientRecordFilter) {
    return jdbc.get().execute(new ClientRecordsCounter(clientRecordFilter));
  }

}
