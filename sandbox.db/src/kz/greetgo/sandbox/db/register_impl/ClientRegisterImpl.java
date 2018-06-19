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

  private String clientRecordsQuery = "select\n" +
    "  client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  client.gender,\n" +
    "  extract(year from age(birth_date)) as age,\n" +
    "  c2.name as charm,\n" +
    "  accountMoneys.min as minBalance,\n" +
    "  accountMoneys.max as maxBalance,\n" +
    "  accountMoneys.sum as accBalance\n" +
    "from client\n" +
    "  join characters c2 on client.charm = c2.id\n" +
    "  left join (select\n" +
    "          clientid,\n" +
    "          SUM(money),\n" +
    "          max(money),\n" +
    "          min(money)\n" +
    "        from client\n" +
    "          join client_account a on client.id = a.clientid\n" +
    "        group by clientid) as accountMoneys on client.id= accountMoneys.clientid";

  @Override
  public <T> List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {
    StringBuilder query = new StringBuilder(clientRecordsQuery);

    if (clientRecordFilter.searchName != null) {
      if (clientRecordFilter.searchName.length() != 0) {
        query.append(" WHERE concat(Lower(name), Lower(surname), Lower(patronymic)) like '%'||?||'%' ");
      } else {
        clientRecordFilter.searchName = null;
      }
    }

    switch (clientRecordFilter.columnName) {
      case "surname":
        query.append(" ORDER BY surname, name, patronymic ASC ");
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

    clientRecordFilter.sliceNum = clientRecordFilter.sliceNum > 0 ? clientRecordFilter.sliceNum : 0;
    clientRecordFilter.paginationPage = clientRecordFilter.paginationPage > 0 ? clientRecordFilter.paginationPage : 0;

    List<T> params = new ArrayList<>();
    if (clientRecordFilter.searchName != null) {
      params.add((T) clientRecordFilter.searchName);
    }
    params.add((T) ((Integer) (clientRecordFilter.sliceNum * clientRecordFilter.paginationPage + clientRecordFilter.sliceNum)));
    params.add((T) ((Integer) (clientRecordFilter.sliceNum * clientRecordFilter.paginationPage)));

    return executeClientRecordsQuery(query.toString(), params);
  }

  private <T> List<ClientRecord> executeClientRecordsQuery(String query, List<T> params) {
    List<ClientRecord> clientRecords = new ArrayList<>();
    jdbc.get().execute(ConnectionCallback -> {
      PreparedStatement statement = ConnectionCallback.prepareStatement(query);

      for (int i = 0; i < params.size(); i++) {
        statement.setObject(i + 1, params.get(i));
      }

      try (ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
          ClientRecord clientRecord = new ClientRecord();
          clientRecord.surname = resultSet.getString("surname");
          clientRecord.name = resultSet.getString("name");
          clientRecord.patronymic = (resultSet.getString("patronymic") != null) ? resultSet.getString("name") : "";
          clientRecord.charm = resultSet.getString("charm");

          clientRecord.age = resultSet.getInt("age");

          clientRecord.maxBalance = resultSet.getDouble("maxBalance");
          clientRecord.minBalance = resultSet.getDouble("minBalance");
          clientRecord.accBalance = resultSet.getDouble("accBalance");

          System.out.println(clientRecord.name + " " + clientRecord.surname);
          clientRecords.add(clientRecord);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return statement;
    });
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
      for (Address address : editedClient.editedAddresses) {
        address.clientId = editedClient.id;
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

    List<Integer> param = new ArrayList<>();
    param.add(editedClient.id);
    return executeClientRecordsQuery(clientRecordsQuery + " where client.id=?", param).get(0);
  }

  private boolean characterIdExists(int id) {
    for (CharmRecord charm : charm()) {
      if (id == charm.id) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<CharmRecord> charm() {
    return clientDao.get().getCharms();
  }

  @Override
  public int getClientCount(ClientRecordFilter clientRecordFilter) {
    return clientDao.get().getClientCount(clientRecordFilter);
  }

}
