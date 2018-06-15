package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.util.ArrayList;
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
    if (filter == null) return new ArrayList<>();

    StringBuilder sqlQuery = new StringBuilder();

    sqlQuery.append("SELECT surname, name, patronymic, DATE_PART('year', '2012-01-01'::date) - DATE_PART('year', '2011-10-02'::date) AS age, AVG(money) AS middle_balance, MAX(money) AS max_balance, MIN(money) AS min_balance ");
    sqlQuery.append("FROM client ");
    sqlQuery.append("LEFT JOIN client_account ON client_account.client=Client.id AND client_account.actual=1 AND client.actual=1 ");

    String direct = sortDirection(filter);
    sqlQuery.append("GROUP BY surname, name, patronymic ");

    if (filter.sortByEnum != null)
      switch (filter.sortByEnum) {
        case FULL_NAME:
          sqlQuery.append(String.format("ORDER BY surname %s, name %s, patronymic %s ", direct, direct, direct));
          break;
        case AGE:
          sqlQuery.append(String.format("ORDER BY age %s ", direct));
          break;
        case MIDDLE_BALANCE:
          sqlQuery.append(String.format("ORDER BY middle_balance %s ", direct));
          break;
        case MAX_BALANCE:
          sqlQuery.append(String.format("ORDER BY max_balance %s ", direct));
          break;
        case MIN_BALANCE:
          sqlQuery.append(String.format("ORDER BY min_balance %s ", direct));
          break;
      }

    if (filter.fio != null)
      sqlQuery.append(String.format("WHERE client.name LIKE %s OR client.surname LIKE %s ", filter.fio, filter.fio));

    sqlQuery.append(String.format("OFFSET %d ", filter.offset));
    sqlQuery.append(String.format("LIMIT %d ", filter.limit));

    return clientDao.get().select(sqlQuery.toString());
  }

  private String sortDirection(ClientFilter clientFilter) {
    if (clientFilter.sortDirection != null) return clientFilter.sortDirection.toString();
    return "";
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
