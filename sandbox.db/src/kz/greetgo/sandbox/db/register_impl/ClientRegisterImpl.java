package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;


  @Override
  public List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {
    StringBuilder query = new StringBuilder("SELECT * FROM v_client_records ");

    if (clientRecordFilter.searchName != null) {
      if (clientRecordFilter.searchName.length() != 0) {
        query.append(" WHERE concat(Lower(name), Lower(surname), Lower(patronymic)) like '%'||?||'%' ");
      }
    }

    switch (clientRecordFilter.columnName) {
      case "surname":
        query.append(" ORDER BY surname,name, patronymic ASC ");
        break;
      case "age":
        query.append(" ORDER BY age ASC ");
        break;
      case "total":
        query.append(" ORDER BY sum ASC ");
        break;
      case "max":
        query.append(" ORDER BY max ASC ");
        break;
      case "min":
        query.append(" ORDER BY min ASC ");
        break;
      case "-surname":
        query.append(" ORDER BY surname, name, patronymic DESC ");
        break;
      case "-age":
        query.append(" ORDER BY age DESC ");
        break;
      case "-total":
        query.append(" ORDER BY sum DESC ");
        break;
      case "-max":
        query.append(" ORDER BY max DESC ");
        break;
      case "-min":
        query.append(" ORDER BY min DESC ");
        break;
    }

    query.append(" LIMIT ? OFFSET ? ");

    //List<ClientRecord> clientRecords = clientDao.get().getClientRecordsAsc(clientRecordFilter);

    List<ClientRecord> clientRecords = new ArrayList<>();

    jdbc.get().execute(ConnectionCallback -> {
      System.out.println("Query: " + query.toString());
      PreparedStatement statement = ConnectionCallback.prepareStatement(query.toString());

      int parameterIndex = 1;
      if (clientRecordFilter.searchName != null) {
        statement.setObject(parameterIndex++, clientRecordFilter.searchName);
      }

      statement.setObject(parameterIndex++, clientRecordFilter.sliceNum * clientRecordFilter.paginationPage + clientRecordFilter.sliceNum);
      statement.setObject(parameterIndex++, clientRecordFilter.sliceNum * clientRecordFilter.paginationPage);

      try (ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
          ClientRecord clientRecord = new ClientRecord();
          clientRecord.surname = resultSet.getString("surname");
          clientRecord.name = resultSet.getString("name");
          clientRecord.patronymic = (resultSet.getString("name") != null) ? resultSet.getString("name") : "";
          clientRecord.character = resultSet.getString("character");

          int firstSpaceIndex = resultSet.getString("age").indexOf(":");
          System.out.println(resultSet.getString("age") + ' ' + firstSpaceIndex);
          String age = resultSet.getString("age").substring(0, firstSpaceIndex);
          clientRecord.age = Integer.parseInt(age);

          clientRecord.maxBalance = resultSet.getDouble("max");
          clientRecord.minBalance = resultSet.getDouble("min");
          clientRecord.accBalance = resultSet.getDouble("sum");

          clientRecords.add(clientRecord);
          System.out.println("From JDBC: " + clientRecord.surname + " " + clientRecord.name + " " + clientRecord.patronymic + " " + clientRecord.age + " " + clientRecord.minBalance + " " + clientRecord.maxBalance + " " + clientRecord.accBalance + " " + clientRecord.character);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return statement;
    });
    return clientRecords;
  }

  private String createSqlQueryFromFilter(ClientRecordFilter filter) {
    return null;
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
