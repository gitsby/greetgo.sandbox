package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.Connection;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;



  @Override
  public List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {


    if (clientRecordFilter.searchName == null) {

    } else {
      // Without where like
      if (clientRecordFilter.searchName.length() == 0) {

      }
    }

    switch (clientRecordFilter.columnName) {
      case "surname":
        System.out.println("SORTING WITH SURNAME");
        clientRecordFilter.columnName = "name";
        break;
      case "gender":
        clientRecordFilter.columnName = "gender";
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

    List<ClientRecord> clientRecords = clientDao.get().getClientRecordsAsc(clientRecordFilter);
    return clientRecords;
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

    if (!characterIdExists(editedClient.charm)) {
      return null;
    }

    if (editedClient.id == null) {
      clientDao.get().insertIntoClient(editedClient);
      editedClient.id = clientDao.get().getLastInsertedClientId();
    } else {
      clientDao.get().updateClient(editedClient);
    }
    if (editedClient.addedAddresses != null) {
      for (Address address : editedClient.addedAddresses) {
        address.clientId = editedClient.id;
        clientDao.get().insertIntoAddress(address);
      }
    }

    if (editedClient.addedPhones != null) {
      for (Phone phone : editedClient.addedPhones) {
        phone.clientid = editedClient.id;
        clientDao.get().insertPhone(phone);
      }
    }

    if (editedClient.editedPhones != null) {
      for (Phone phone : editedClient.editedPhones) {
        phone.clientid = editedClient.id;
        clientDao.get().updatePhone(phone);
      }
    }

    if (editedClient.editedAddresses != null) {
      System.out.println("EDITING ADDRESS");
      for (Address address : editedClient.editedAddresses) {
        address.clientId = editedClient.id;
        System.out.println(address.flat + " " + address.street + " " + address.house + " " + address.type + " " + address.clientId);
        clientDao.get().updateAddress(address);
      }
    }

    if (editedClient.deletedPhones != null) {
      for (Phone phone : editedClient.deletedPhones) {
        phone.clientid = editedClient.id;
        clientDao.get().deletePhone(phone);
      }
    }
    if (editedClient.deletedAddresses != null) {
      for (Address address : editedClient.deletedAddresses) {
        clientDao.get().deleteAddress(address);
      }
    }

    return null;
  }

  private boolean characterIdExists(int id) {
    for (CharmRecord charecter :
      charm()) {
      if (id == charecter.id) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<CharmRecord> charm() {
    return clientDao.get().getCharacters();
  }

  @Override
  public int getClientCount(ClientRecordFilter clientRecordFilter) {
    return clientDao.get().getClientRecords(clientRecordFilter).size();
  }

}
