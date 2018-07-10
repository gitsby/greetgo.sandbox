package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public ClientDetails detail(Integer clientId) {
    ClientDot clientDot = getClient(clientId);
    return toClientDetail(clientDot);
  }

  @Override
  public ClientRecord save(ClientToSave clientToSave) {
    ClientDot clientDot;
    if (clientToSave.id == null) {
      clientDot = new ClientDot();
      db.get().clientsStorage.add(clientDot);
      clientDot.id = db.get().clientsStorage.size();
    } else clientDot = getClient(clientToSave.id);
    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    clientDot.gender = clientToSave.gender;
    clientDot.birthDate = clientToSave.birthDate;
    saveClientAddress(clientDot.id, clientToSave.addressFact);
    saveClientAddress(clientDot.id, clientToSave.addressReg);
    saveClientPhone(clientDot.id, clientToSave.homePhone);
    saveClientPhone(clientDot.id, clientToSave.workPhone);
    saveClientPhone(clientDot.id, clientToSave.mobilePhone);
    clientDot.charmId = clientToSave.charmId;
    return toClientRecords(clientDot);
  }

  @Override
  public void delete(Integer clientId) {
    db.get().clientsStorage.removeIf(clientDot -> clientDot.id.equals(clientId));
  }

  @Override
  public List<ClientRecord> getRecords(ClientFilter clientFilter) {
    List<ClientRecord> clientRecords = getAllRecordWithFilter(clientFilter);

    if (clientFilter.limit < 0) clientFilter.limit = 0;
    if (clientFilter.offset < 0) clientFilter.offset = 0;

    if (clientFilter.limit > clientRecords.size()) clientFilter.limit = clientRecords.size();
    if (clientFilter.offset > clientRecords.size()) clientFilter.offset = clientRecords.size();

    return clientRecords.subList(clientFilter.offset, clientFilter.limit);
  }

  @Override
  public int getRecordsCount(ClientFilter clientFilter) {
    return getRecordsList(clientFilter).size();
  }

  @Override
  public List<CharmRecord> getCharms() {
    return db.get().charms.stream().map(CharmDot::toCharm).collect(Collectors.toList());
  }

  @Override
  public void renderClientList(String name, ClientFilter clientFilter, ClientRender render) {
    List<ClientRecord> clientRecords = getAllRecordWithFilter(clientFilter);
    render.start(name, new Date());
    for (ClientRecord clientRecord : clientRecords) {
      render.append(clientRecord);
    }
    render.finish();
  }

  private List<ClientRecord> getAllRecordWithFilter(ClientFilter clientFilter) {
    List<ClientRecord> clientRecords = getRecordsList(clientFilter);

    Comparator<ClientRecord> comparator = null;
    if (clientFilter.sortByEnum != null)
      switch (clientFilter.sortByEnum) {
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

    if (clientFilter.sortDirection != null)
      if (clientFilter.sortDirection == SortDirection.DESCENDING) Collections.reverse(clientRecords);
    return clientRecords;
  }

  private ClientDot getClient(int clientId) {
    return db.get().clientsStorage.stream().filter(clientDot -> clientDot.id == clientId).findFirst().get();
  }

  private List<ClientRecord> getRecordsList(ClientFilter clientFilter) {
    List<ClientRecord> clientRecords = new ArrayList<>();

    for (ClientDot clientDot : db.get().clientsStorage) {
      if (clientFilter != null && clientFilter.fio != null) {
        if ((clientDot.name.contains(clientFilter.fio))
          || (clientDot.surname.contains(clientFilter.fio))) {
          clientRecords.add(toClientRecords(clientDot));
        }
      } else {
        clientRecords.add(toClientRecords(clientDot));
      }
    }
    return clientRecords;
  }

  private int getAge(Date d) {
    Calendar curr = Calendar.getInstance();
    Calendar birth = Calendar.getInstance();
    birth.setTime(d);
    int yeardiff = curr.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
    curr.add(Calendar.YEAR, -yeardiff);
    if (birth.after(curr)) {
      yeardiff = yeardiff - 1;
    }
    return yeardiff;
  }

  private float getMiddleBalance(ClientDot clientDot) {
    float middle_balance = 0;
    List<ClientAccount> clientAccounts = db.get().accounts.stream().map(ClientAccountDot::toClientAccount).filter(clientAccountDot -> clientAccountDot.client.equals(clientDot.id)).collect(Collectors.toList());
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      middle_balance += clientAccount.money;
    return middle_balance / clientAccounts.size();
  }

  private float getMaxBalance(ClientDot clientDot) {
    float max_balance = -1;
    List<ClientAccount> clientAccounts = db.get().accounts.stream().map(ClientAccountDot::toClientAccount).filter(clientAccountDot -> clientAccountDot.client.equals(clientDot.id)).collect(Collectors.toList());
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money > max_balance) max_balance = clientAccount.money;
    return max_balance;
  }

  private float getMinBalance(ClientDot clientDot) {
    float min_balance = Integer.MAX_VALUE;
    List<ClientAccount> clientAccounts = db.get().accounts.stream().map(ClientAccountDot::toClientAccount).filter(clientAccountDot -> clientAccountDot.client.equals(clientDot.id)).collect(Collectors.toList());
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money < min_balance) min_balance = clientAccount.money;
    return min_balance;
  }

  private ClientRecord toClientRecords(ClientDot clientDot) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = clientDot.id;
    clientRecord.name = clientDot.name;
    clientRecord.surname = clientDot.surname;
    clientRecord.patronymic = clientDot.patronymic;
    clientRecord.age = getAge(clientDot.birthDate);
    clientRecord.middle_balance = getMiddleBalance(clientDot);
    clientRecord.max_balance = getMaxBalance(clientDot);
    clientRecord.min_balance = getMinBalance(clientDot);
    return clientRecord;
  }

  private ClientDetails toClientDetail(ClientDot clientDot) {
    ClientDetails details = new ClientDetails();
    details.id = clientDot.id;
    details.name = clientDot.name;
    details.surname = clientDot.surname;
    details.patronymic = clientDot.patronymic;
    details.birthDate = clientDot.birthDate;
    details.charmId = clientDot.charmId;
    details.addressFact = getClientAddress(clientDot.id, AddressTypeEnum.FACT);
    details.addressReg = getClientAddress(clientDot.id, AddressTypeEnum.REG);
    details.homePhone = getClientPhone(clientDot.id, PhoneType.HOME);
    details.mobilePhone = getClientPhone(clientDot.id, PhoneType.MOBILE);
    details.workPhone = getClientPhone(clientDot.id, PhoneType.WORK);
    details.gender = clientDot.gender;
    return details;
  }

  private ClientAddress getClientAddress(int client, AddressTypeEnum type) {
    return db.get().addresses.stream().filter(clientAddress -> clientAddress.client.equals(client) && clientAddress.type.equals(type)).findFirst().get().toClientAddress();
  }

  private ClientPhone getClientPhone(int client, PhoneType type) {
    return db.get().phones.stream().filter(clientPhone -> clientPhone.client.equals(client) && clientPhone.type.equals(type)).findFirst().get().toClientPhone();
  }

  private ClientAddress saveClientAddress(Integer client, ClientAddress saveClientAddress) {
    System.out.println(client);
    if (saveClientAddress.client == null) {
      saveClientAddress.client = client;
      db.get().addresses.add(new ClientAddressDot(saveClientAddress));
      return saveClientAddress;
    } else {
      ClientAddress clientAddress = getClientAddress(saveClientAddress.client, saveClientAddress.type);
      clientAddress.street = saveClientAddress.street;
      clientAddress.house = saveClientAddress.house;
      clientAddress.flat = saveClientAddress.flat;
      return clientAddress;
    }
  }

  private ClientPhone saveClientPhone(Integer client, ClientPhone saveClientPhone) {
    if (saveClientPhone.client == null) {
      saveClientPhone.client = client;
      db.get().phones.add(new ClientPhoneDot(saveClientPhone));
      return saveClientPhone;
    } else {
      ClientPhone clientPhone = getClientPhone(saveClientPhone.client, saveClientPhone.type);
      clientPhone.number = saveClientPhone.number;
      clientPhone.type = saveClientPhone.type;
      return clientPhone;
    }
  }
}
