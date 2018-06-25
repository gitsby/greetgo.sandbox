package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.util.RND;
import org.testng.collections.Lists;

import java.util.*;

public abstract class ClientTestDaoHelper implements ClientTestDao {

  public List<ClientDetails> getClientDetailsList() {
    List<Client> clients = getClients();
    List<ClientDetails> clientDetails = Lists.newArrayList();
    for (Client client : clients) {
      ClientDetails details = new ClientDetails();
      details.id = client.id;
      details.surname = client.surname;
      details.name = client.name;
      details.patronymic = client.patronymic;
      details.gender = client.gender;
      details.birthDate = client.birthDate;
      details.charmId = client.charmId;
      details.addressFact = getClientAddress(client.id, AddressTypeEnum.FACT);
      details.addressReg = getClientAddress(client.id, AddressTypeEnum.REG);
      details.homePhone = getClientPhone(client.id, PhoneType.HOME);
      details.workPhone = getClientPhone(client.id, PhoneType.WORK);
      details.mobilePhone = getClientPhone(client.id, PhoneType.MOBILE);
      clientDetails.add(details);
    }
    return clientDetails;
  }

  public ClientRecord getRecordsFromDetails(ClientDetails details) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = details.id;
    clientRecord.surname = details.surname;
    clientRecord.name = details.name;
    clientRecord.patronymic = details.patronymic;
    clientRecord.age = getAge(details.birthDate);
    List<ClientAccount> clientAccounts = getClientAccounts(details.id);
    clientRecord.middle_balance = getMiddleBalance(clientAccounts);
    clientRecord.max_balance = getMaxBalance(clientAccounts);
    clientRecord.min_balance = getMinBalance(clientAccounts);
    return clientRecord;
  }

  public static int getAge(Date dateOfBirth) {
    Calendar today = Calendar.getInstance();
    Calendar birthDate = Calendar.getInstance();
    int age;
    birthDate.setTime(dateOfBirth);
    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
    if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
      (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
      age--;
    }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
      (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
      age--;
    }
    return ++age;
  }
  private float getMinBalance(List<ClientAccount> clientAccounts) {
    float min_balance = Integer.MAX_VALUE;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money < min_balance) min_balance = clientAccount.money;
    return min_balance;
  }

  private float getMaxBalance(List<ClientAccount> clientAccounts) {
    float max_balance = -1;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money > max_balance) max_balance = clientAccount.money;
    return max_balance;
  }

  private float getMiddleBalance(List<ClientAccount> clientAccounts) {
    float middle_balance = 0;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      middle_balance += clientAccount.money;
    return middle_balance / clientAccounts.size();
  }

  public void insertClient(ClientDetails details) {
    CharmRecord charmRecord = new CharmRecord(details.charmId, RND.str(10), RND.str(10), (float)RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charmId);
    insertClientAddress(details.addressFact);
    insertClientAddress(details.addressReg);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);
  }

  public void insertClient(ClientDot clientDot) {
    CharmRecord charmRecord = new CharmRecord(clientDot.charmId, RND.str(10), RND.str(10), (float)RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    insertClient(clientDot.id, clientDot.surname, clientDot.name, clientDot.patronymic, clientDot.gender, clientDot.birthDate, clientDot.charmId);
  }

  public ClientToSave generateRandomClientToSave(Integer id) {
    ClientDetails details = generateRandomClientDetails(id);
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.id = details.id;
    clientToSave.surname = details.surname;
    clientToSave.name = details.name;
    clientToSave.patronymic = details.patronymic;
    clientToSave.birthDate = details.birthDate;
    clientToSave.gender = details.gender;
    clientToSave.charmId = details.charmId;
    clientToSave.addressFact = details.addressFact;
    clientToSave.addressReg = details.addressReg;
    clientToSave.homePhone = details.homePhone;
    clientToSave.mobilePhone = details.mobilePhone;
    clientToSave.workPhone = details.workPhone;
    CharmRecord charmRecord = new CharmRecord(details.charmId, RND.str(10), RND.str(10), (float)RND.plusDouble(Double.MAX_VALUE, 10));
    insertCharm(charmRecord);
    return clientToSave;
  }

  public ClientDot generateRandomClientDot() {
    ClientDot clientDot = new ClientDot();
    clientDot.id = RND.plusInt(Integer.MAX_VALUE);
    clientDot.surname = RND.str(10);
    clientDot.name = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.birthDate = RND.dateYears(10, 20);
    clientDot.charmId = RND.plusInt(Integer.MAX_VALUE);
    clientDot.gender = GenderEnum.MALE;
    return clientDot;
  }

  public ClientDetails generateRandomClientDetails(Integer id) {
    ClientDetails details = new ClientDetails();
    details.id = id;
    details.surname = RND.intStr(10);
    details.name = RND.intStr(10);
    details.patronymic = RND.intStr(10);
    details.birthDate = RND.dateYears(0, 1000);
    details.gender = GenderEnum.MALE;
    details.charmId = RND.plusInt(Integer.MAX_VALUE);
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    return details;
  }

  public void generateRandomAccountsFor(Integer id, int i) {
    for (int c = 0; c < i; c++) {
      ClientAccount clientAccount = new ClientAccount();
      clientAccount.client = id;
      clientAccount.number = RND.intStr(11);
      clientAccount.money = (float) RND.plusDouble(5000, 0);
      clientAccount.registeredAt = new Date();
      insertClientAccount(clientAccount);
    }
  }

  private void insertClientAddress(ClientAddress clientAddress) {
    insertClientAddress(clientAddress.client, clientAddress.type, clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private void insertClientPhone(ClientPhone clientPhone) {
    insertClientPhone(clientPhone.client, clientPhone.number, clientPhone.type);
  }

  private void insertClientAccount(ClientAccount account) {
    insertClientAccount(account.client, account.number, account.money, account.registeredAt);
  }

  public void insertCharm(CharmRecord charmRecord) {
    insertCharm(charmRecord.id, charmRecord.name, charmRecord.description, charmRecord.energy);
  }

  public static void sortList(List<ClientRecord> clientRecords, SortByEnum sortBy, SortDirection sortDirection) {
    Comparator<ClientRecord> comparator = null;
    switch (sortBy) {
      case NONE:
        comparator = null;
        break;
      case FULL_NAME:
        comparator = Comparator.comparing(o -> o.surname);
        comparator.thenComparing(o -> o.name);
        comparator.thenComparing(o -> o.patronymic);
        break;
      case AGE:
        comparator = Comparator.comparing(o -> o.age);
        break;
      case MIDDLE_BALANCE:
        comparator = Comparator.comparing(o -> o.middle_balance);
        break;
      case MIN_BALANCE:
        comparator = Comparator.comparing(o -> o.min_balance);
        break;
      case MAX_BALANCE:
        comparator = Comparator.comparing(o -> o.max_balance);
        break;
    }
    if (comparator != null) clientRecords.sort(comparator);

    if (sortDirection != null)
      switch (sortDirection) {
        case DESCENDING:
          Collections.reverse(clientRecords);
          break;
      }
  }
}
