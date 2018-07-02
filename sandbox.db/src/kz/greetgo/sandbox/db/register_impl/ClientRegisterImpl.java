package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.client_records_query.ClientRecordsCounter;
import kz.greetgo.sandbox.db.client_records_query.ClientRecordsQuery;
import kz.greetgo.sandbox.db.client_records_query.ClientRecordsRender;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordsViewPdf;
import kz.greetgo.sandbox.db.client_records_report.ClientRecordsViewXlsx;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.OutputStream;
import java.util.Date;
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
    if (!characterIdExists(editedClient.charm)) {
      return null;
    }

    if (editedClient.id == null) {
      editedClient.id = clientDao.get().insertClient(editedClient);
    } else {
      clientDao.get().updateClient(editedClient);
    }
    saveAddresses(editedClient.addedAddresses, "add", editedClient.id);
    saveAddresses(editedClient.editedAddresses, "edit", editedClient.id);
    saveAddresses(editedClient.deletedAddresses, "delete", editedClient.id);

    savePhones(editedClient.addedPhones, "add", editedClient.id);
    savePhones(editedClient.editedPhones, "edit", editedClient.id);
    savePhones(editedClient.deletedPhones, "delete", editedClient.id);


    ClientRecordFilter filter = new ClientRecordFilter();
    filter.paginationPage = 0;
    filter.sliceNum = 1;
    filter.columnName = "empty";

    ClientRecordsQuery query = new ClientRecordsQuery(filter);
    query.sql.WHERE("client.id=?");
    query.params.add(0, editedClient.id);

    return jdbc.get().execute(query).get(0);
  }

  private void savePhones(List<Phone> phones, String type, int clientId) {
    if (phones != null) {
      for (Phone phone : phones) {
        phone.client_id = clientId;
        switch (type) {
          case "add":
            clientDao.get().insertPhone(phone);
            break;
          case "edit":
            clientDao.get().updatePhone(phone);
            break;
          case "delete":
            clientDao.get().deletePhone(phone);
            break;
        }
      }
    }
  }

  @Override
  public void renderClientList(ClientRecordFilter filter, String userName, String type, OutputStream outputStream) throws Exception {
    ClientRecordsReportView reportView;
    switch (type) {
      case "pdf":
        reportView = new ClientRecordsViewPdf(outputStream);
        break;
      case "xlsx":
        reportView = new ClientRecordsViewXlsx(outputStream);
        break;
      default:
        throw new Exception("Not existing type");
    }
    jdbc.get().execute(new ClientRecordsRender(filter, reportView));
    reportView.finish(userName, new Date());
  }

  private void saveAddresses(List<Address> addresses, String type, int clientId) {
    if (addresses != null) {
      for (Address address : addresses) {
        address.clientId = clientId;
        switch (type) {
          case "add":
            clientDao.get().insertAddress(address);
            break;
          case "edit":
            clientDao.get().updateAddress(address);
            break;
          case "delete":
            clientDao.get().deleteAddress(address);
            break;
        }
      }
    }

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
    return jdbc.get().execute(new ClientRecordsCounter(clientRecordFilter));
  }

}
