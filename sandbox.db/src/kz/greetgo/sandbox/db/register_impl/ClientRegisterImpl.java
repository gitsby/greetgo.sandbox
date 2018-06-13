package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public List<ClientRecord> getClients(ClientRecordPhilter clientRecordPhilter) {
    return null;
  }

  @Override
  public void deleteClient(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientDetails getClientById(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientRecord editedClient(ClientToSave editedClient) {
    if (editedClient.id != null) {
      System.out.println("INSERTING INTO");

    }
    return null;
  }

  @Override
  public List<Character> getCharacters() {
    return clientDao.get().getCharacters();
  }

  @Override
  public int getRequestedPaginationNum(ClientRecordPhilter clientRecordPhilter) {
    return 0;
  }

  private void createNewClient(ClientToSave client) {

    clientDao.get().insertIntoClient(client);

    for (Address address : client.addedAddresses) {
      clientDao.get().insertIntoAddress(address);
    }
    for (Phone phone : client.addedPhones) {
      clientDao.get().insertPhones(phone);
    }

  }

}
