package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.callback.*;
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

  // FIXME: 6/28/18 Должен возвращать рекорд
  @Override
  public Integer save(ClientToSave clientToSave) {
    return insertOrUpdateClient(clientToSave);
  }

  private Integer insertOrUpdateClient(ClientToSave clientToSave) {
    // FIXME: 6/28/18 инсерт/апдейт клиента не должен быть разделен на разные куски кода в логический разных классах
    int res = jdbc.get().execute(clientToSave.id == null ? new InsertClientCallback(clientToSave) : new UpdateClientCallback(clientToSave));
    insertPhonesAndAddresses(res, clientToSave);
    return res;
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
    return jdbc.get().execute(new ClientPhoneCallback(clientId, type));
  }

  private ClientAddress getClientAddress(Integer clientId, AddressTypeEnum type) {
    // FIXME: 6/28/18 Можно же через iBatis сделать и уменшить объем кода
    return jdbc.get().execute(new ClientAddressCallback(clientId, type));
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
    // FIXME: 6/28/18 Takje mojno cherez iBatis
    return jdbc.get().execute(new CharListCallback());
  }

  @Override
  public void renderClientList(String name, ClientFilter filter, ClientRender render) {
    jdbc.get().execute(new ClientRenderCallback(name, filter, render));
  }
}
