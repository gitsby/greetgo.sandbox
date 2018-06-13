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
  public List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {
    if (clientRecordFilter.searchName != null) {
      // Without where like
      if (clientRecordFilter.searchName.length() == 0) {

      }

    }

    System.out.println("EXECUTING");
    switch (clientRecordFilter.columnName) {
      case "surname":
        clientRecordFilter.searchName = "client.name";
        break;
      case "age":
        break;
      case "total":
        break;
      case "max":
        break;
      case "min":
        break;
      case "-surname":
        break;
      case "-age":
        break;
      case "-total":
        break;
      case "-max":
        break;
      case "-min":
        break;
    }

    List<ClientRecord> clientRecords = clientDao.get().getClientRecords(clientRecordFilter);
    return clientRecords;
  }

  @Override
  public void deleteClient(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientDetails getClientDetails(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientRecord save(ClientToSave editedClient) {
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
  public int getClientCount(ClientRecordFilter clientRecordFilter) {
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
