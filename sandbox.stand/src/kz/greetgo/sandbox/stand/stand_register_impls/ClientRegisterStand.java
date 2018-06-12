package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public ClientInfo get(int clientId) {
    return null;
  }

  @Override
  public ClientDetail detail(int clientId) {
    ClientDot clientDot = getClient(clientId);
    return toClientDetail(clientDot);
  }

  @Override
  public void save(ClientToSave clientToSave) {
    ClientDot clientDot;
    if (clientToSave.id == null) {
      clientDot = new ClientDot();
      db.get().clientsStorage.add(0, clientDot);
      clientDot.id = db.get().clientsStorage.size();
    }
    else clientDot = getClient(clientToSave.id);
    System.out.println(clientToSave.birth_day);
    clientDot.name = clientToSave.name;
    clientDot.surname = clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic;
    clientDot.gender = clientToSave.gender;
    clientDot.birth_day = clientToSave.birth_day;
    clientDot.addressFactId = saveClientAddress(clientToSave.addressFact).id;
    clientDot.addressRegId = saveClientAddress(clientToSave.addressReg).id;
    clientDot.homePhoneId = saveClientPhone(clientToSave.homePhone).id;
    clientDot.workPhoneId = saveClientPhone(clientToSave.workPhone).id;
    clientDot.mobilePhoneId = saveClientPhone(clientToSave.mobilePhone).id;
    clientDot.charmId = clientToSave.charmId;
  }

  @Override
  public void remove(int clientId) {
    db.get().clientsStorage.removeIf(clientDot -> clientDot.id == clientId);
  }

  @Override
  public List<ClientRecords> getRecords(ClientFilter clientFilter) {
    List<ClientRecords> clientRecords = getRecordsList(clientFilter);

    Comparator<ClientRecords> comparator = null;
    if (clientFilter.sortBy != null)
      switch (clientFilter.sortBy) {
        case NONE:
          comparator = null;
          break;
        case NAME:
          comparator = Comparator.comparing(o -> o.name);
          break;
        case SURNAME:
          comparator = Comparator.comparing(o -> o.surname);
          break;
        case PATRONYMIC:
          comparator = Comparator.comparing(o -> o.patronymic);
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

    if (clientFilter.to < 0) clientFilter.to = 0;
    if (clientFilter.from < 0) clientFilter.from = 0;

    if (clientFilter.to > clientRecords.size()) clientFilter.to = clientRecords.size();
    if (clientFilter.from > clientRecords.size()) clientFilter.from = clientRecords.size();

    return clientRecords.subList(clientFilter.from, clientFilter.to);
  }

  @Override
  public int getRecordsCount(ClientFilter clientFilter) {
    return getRecordsList(clientFilter).size();
  }

  @Override
  public List<Charm> getCharms() {
    return db.get().charms;
  }

  private ClientDot getClient(int clientId) {
    return db.get().clientsStorage.stream().filter(clientDot -> clientDot.id == clientId).findFirst().get();
  }

  private List<ClientRecords> getRecordsList(ClientFilter clientFilter) {
    List<ClientDot> temp = db.get().clientsStorage;
    List<ClientRecords> clientRecords = new ArrayList<>();

    for(ClientDot clientDot : temp) {
      if (clientFilter != null && clientFilter.fio != null) {
        if ((clientDot.name.contains(clientFilter.fio))
          || (clientDot.surname.contains(clientFilter.fio))
          || (clientDot.patronymic.contains(clientFilter.fio))) {
          clientRecords.add(toClientRecords(clientDot));
        }
      }
      else {
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
    curr.add(Calendar.YEAR,-yeardiff);
    if(birth.after(curr))
    {
      yeardiff = yeardiff - 1;
    }
    return yeardiff;
  }

  private float getMiddleBalance(ClientDot clientDot) {
    float middle_balance = 0;
    for (int clientAccountId : clientDot.accountsId)
      middle_balance += getClientAccount(clientAccountId).money;
    return middle_balance / clientDot.accountsId.size();
  }

  private float getMaxBalance(ClientDot clientDot) {
    if (clientDot.accountsId.size() == 0) return 0;
    float max_balance = -1;
    for (int clientAccountId : clientDot.accountsId) {
      ClientAccount clientAccount = getClientAccount(clientAccountId);
      if (clientAccount.money > max_balance) max_balance = clientAccount.money;
    }
    return max_balance;
  }

  private float getMinBalance(ClientDot clientDot) {
    if (clientDot.accountsId.size() == 0) return 0;
    float min_balance = Integer.MAX_VALUE;
    for (int clientAccountId : clientDot.accountsId) {
      ClientAccount clientAccount = getClientAccount(clientAccountId);
      if (clientAccount.money < min_balance) min_balance = clientAccount.money;
    }
    return min_balance;
  }

  private ClientRecords toClientRecords (ClientDot clientDot) {
    ClientRecords clientRecords = new ClientRecords();
    clientRecords.id = clientDot.id;
    clientRecords.name = clientDot.name;
    clientRecords.surname = clientDot.surname;
    clientRecords.patronymic = clientDot.patronymic;
    clientRecords.age = getAge(clientDot.birth_day);
    clientRecords.middle_balance = getMiddleBalance(clientDot);
    clientRecords.max_balance = getMaxBalance(clientDot);
    clientRecords.min_balance = getMinBalance(clientDot);
    return clientRecords;
  }

  private ClientDetail toClientDetail(ClientDot clientDot) {
    ClientDetail clientDetail = new ClientDetail();
    clientDetail.id = clientDot.id;
    clientDetail.name = clientDot.name;
    clientDetail.surname = clientDot.surname;
    clientDetail.patronymic = clientDot.patronymic;
    clientDetail.birth_day = clientDot.birth_day;
    clientDetail.charm = getCharms().get(clientDot.charmId);
    clientDetail.addressFact = getClientAddress(clientDot.addressFactId);
    clientDetail.addressReg = getClientAddress(clientDot.addressRegId);
    clientDetail.homePhone = getClientPhone(clientDot.homePhoneId);
    clientDetail.mobilePhone = getClientPhone(clientDot.mobilePhoneId);
    clientDetail.workPhone = getClientPhone(clientDot.workPhoneId);
    clientDetail.gender = clientDot.gender;
    return clientDetail;
  }

  private ClientAccount getClientAccount(int clientAccountId) {
    return db.get().accounts.stream().filter(clientAccount -> clientAccount.id == clientAccountId).findFirst().get();
  }

  private ClientAddress getClientAddress(int clientAddressId) {
    return db.get().addresses.stream().filter(clientAddress -> clientAddress.id == clientAddressId).findFirst().get();
  }

  private ClientPhone getClientPhone(int clientPhoneId) {
    return db.get().phones.stream().filter(clientPhone -> clientPhone.id == clientPhoneId).findFirst().get();
  }

  private ClientAddress saveClientAddress(ClientAddress saveClientAddress) {
    if(saveClientAddress.id == null) {
      saveClientAddress.id = db.get().addresses.size();
      db.get().addresses.add(saveClientAddress);
      return saveClientAddress;
    } else {
      ClientAddress clientAddress = getClientAddress(saveClientAddress.id);
      clientAddress.street = saveClientAddress.street;
      clientAddress.house = saveClientAddress.house;
      clientAddress.flat = saveClientAddress.flat;
      return clientAddress;
    }
  }

  private ClientPhone saveClientPhone(ClientPhone saveClientPhone) {
    if(saveClientPhone.id == null) {
      saveClientPhone.id = db.get().phones.size();
      db.get().phones.add(saveClientPhone);
      return saveClientPhone;
    } else {
      ClientPhone clientPhone = getClientPhone(saveClientPhone.id);
      clientPhone.number = saveClientPhone.number;
      clientPhone.type = saveClientPhone.type;
      return clientPhone;
    }
  }
}
