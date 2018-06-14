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
    return getDetails(clientId);
  }

  @Override
  public void save(ClientToSave clientToSave) {
    if (clientToSave.id == null) {

      Integer id = clientDao.get().insert(clientToSave.surname, clientToSave.name, clientToSave.patronymic, clientToSave.gender, clientToSave.birthDate, clientToSave.charmId);
      clientToSave.addressFact.client = id;
      insertAddress(clientToSave.addressFact);
      clientToSave.addressReg.client = id;
      insertAddress(clientToSave.addressReg);
      clientToSave.homePhone.client = id;
      insertPhone(clientToSave.homePhone);
      clientToSave.workPhone.client = id;
      insertPhone(clientToSave.workPhone);
      clientToSave.mobilePhone.client = id;
      insertPhone(clientToSave.mobilePhone);

    } else {

      Details details = getDetails(clientToSave.id);
      clientDao.get().update(clientToSave.id, clientToSave.surname, clientToSave.name, clientToSave.patronymic, clientToSave.gender, clientToSave.birthDate, clientToSave.charmId);
      checkAddresses(details.addressFact, clientToSave.addressFact);
      checkAddresses(details.addressReg, clientToSave.addressReg);
      checkPhone(details.homePhone, clientToSave.homePhone);
      checkPhone(details.workPhone, clientToSave.workPhone);
      checkPhone(details.mobilePhone, clientToSave.mobilePhone);
    }
  }

  private Details getDetails(Integer clientId) {
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

  private void checkAddresses(ClientAddress currentAddress, ClientAddress editedAddress) {
    if (currentAddress.equals(editedAddress)) return;
    clientDao.get().updateAddress(currentAddress.client, editedAddress.type, editedAddress.street, editedAddress.house, editedAddress.flat);
  }

  private void checkPhone(ClientPhone currentPhone, ClientPhone editedPhone) {
    if (currentPhone.equals(editedPhone)) return;
    clientDao.get().updatePhone(currentPhone.client, currentPhone.number, editedPhone.type, editedPhone.number);
  }

  private void insertAddress(ClientAddress clientAddress) {
    clientDao.get().insertAddress(clientAddress.client, clientAddress.type, clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private void insertPhone(ClientPhone clientPhone) {
    clientDao.get().insertPhone(clientPhone.client, clientPhone.type, clientPhone.number);
  }

  @Override
  public void delete(Integer clientId) {
    clientDao.get().updateField(clientId, "actual", 0);
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
