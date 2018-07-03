package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.callback.ClientRecordListCallback;
import kz.greetgo.sandbox.db.register_impl.jdbc.callback.ClientRecordsCountCallback;
import kz.greetgo.sandbox.db.register_impl.jdbc.callback.ClientRenderCallback;
import kz.greetgo.sandbox.db.register_impl.jdbc.callback.InsertClientCallback;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientDetails detail(Integer clientId) {
    return getDetails(clientId);
  }

  private ClientDetails getDetails(Integer clientId) {
    ClientDetails details = clientDao.get().getDetails(clientId);
    details.addressFact = getClientAddress(clientId, AddressTypeEnum.FACT);
    details.addressReg = getClientAddress(clientId, AddressTypeEnum.REG);
    details.homePhone = getClientPhone(clientId, PhoneType.HOME);
    details.workPhone = getClientPhone(clientId, PhoneType.WORK);
    details.mobilePhone = getClientPhone(clientId, PhoneType.MOBILE);
    return details;
  }

  @Override
  public ClientRecord save(ClientToSave clientToSave) {
    return insertOrUpdateClient(clientToSave);
  }

  private ClientRecord insertOrUpdateClient(ClientToSave clientToSave) {
    int res = jdbc.get().execute(new InsertClientCallback(clientToSave));
    insertPhonesAndAddresses(res, clientToSave);
    return clientDao.get().getClientRecord(res);
  }

  private void insertPhonesAndAddresses(Integer client, ClientToSave clientToSave) {
    clientToSave.addressFact.client = client;
    insertOrUpdateClientAddress(clientToSave.addressFact);
    clientToSave.addressReg.client = client;
    insertOrUpdateClientAddress(clientToSave.addressReg);
    clientToSave.homePhone.client = client;
    insertOrUpdateClientPhone(clientToSave.homePhone);
    clientToSave.workPhone.client = client;
    insertOrUpdateClientPhone(clientToSave.workPhone);
    clientToSave.mobilePhone.client = client;
    insertOrUpdateClientPhone(clientToSave.mobilePhone);
  }

  private void insertOrUpdateClientPhone(ClientPhone clientPhone) {
    clientDao.get().insertPhone(clientPhone.client, clientPhone.type.name(), clientPhone.number);
  }

  private void insertOrUpdateClientAddress(ClientAddress clientAddress) {
    clientDao.get().insertAddress(clientAddress.client, clientAddress.type.name(), clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private ClientPhone getClientPhone(Integer clientId, PhoneType type) {
    return clientDao.get().getClientPhone(clientId, type);
  }

  private ClientAddress getClientAddress(Integer clientId, AddressTypeEnum type) {
    return clientDao.get().getClientAddress(clientId, type.name());
  }

  @Override
  public void delete(Integer clientId) {
    clientDao.get().setNotActual(clientId);
  }

  @Override
  public List<ClientRecord> getRecords(ClientFilter filter) {
    if (filter == null) return new ArrayList<>();
    return jdbc.get().execute(new ClientRecordListCallback(filter));
  }

  @Override
  public int getRecordsCount(ClientFilter filter) {
    if (filter == null) return 0;
    return jdbc.get().execute(new ClientRecordsCountCallback(filter));
  }

  @Override
  public List<CharmRecord> getCharms() {
    return clientDao.get().getCharms();
  }

  @Override
  public void renderClientList(String name, ClientFilter filter, ClientRender render) {
    jdbc.get().execute(new ClientRenderCallback(name, filter, render));
  }
}
