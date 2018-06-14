package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public Details detail(Integer clientId) {
    Client client = clientDao.get().get(clientId);
    Details details = new Details();
    details.id = clientId;
    details.surname = client.surname;
    details.name = client.name;
    details.patronymic = client.patronymic;
    details.gender = client.gender;
    details.charm = clientDao.get().getCharm(client.charm);
    details.addressFact = clientDao.get().getAddress(clientId, AddressTypeEnum.FACT);
    details.addressReg = clientDao.get().getAddress(clientId, AddressTypeEnum.REG);
    details.homePhone = clientDao.get().getPhone(clientId, PhoneType.HOME);
    details.workPhone = clientDao.get().getPhone(clientId, PhoneType.WORK);
    details.mobilePhone = clientDao.get().getPhone(clientId, PhoneType.MOBILE);
    return details;
  }

  @Override
  public void save(ClientToSave clientToSave) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Integer clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ClientRecord> getRecords(ClientFilter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRecordsCount(ClientFilter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Charm> getCharms() {
    throw new UnsupportedOperationException();
  }
}
