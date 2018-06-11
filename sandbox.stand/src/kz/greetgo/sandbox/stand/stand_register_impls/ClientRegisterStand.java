package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  @Override
  public ClientDetail get(int clientId) {
    ClientDot clientDot = getClient(clientId);
    if (clientDot == null) return null;
    else return clientDot.toClientDetail();
  }

  @Override
  public void save(ClientToSave clientToSave) {
    ClientDot clientDot;
    if (clientToSave.id == null) {
      clientDot = new ClientDot();
      db.get().clientsStorage.add(clientDot);
      clientDot.id = db.get().clientsStorage.size();
    }
    else clientDot = getClient(clientToSave.id);
    assert clientDot != null;
    clientDot.name = clientToSave.name == null ? "" : clientToSave.name;
    clientDot.surname = clientToSave.surname == null ? "" : clientToSave.surname;
    clientDot.patronymic = clientToSave.patronymic == null ? "" : clientToSave.patronymic;
    clientDot.gender = clientToSave.gender;
    clientDot.birth_day = clientToSave.birth_day;
    clientDot.addressFact = clientToSave.addressFact;
    clientDot.addressReg = clientToSave.addressReg;
    clientDot.homePhone = clientToSave.homePhone;
    clientDot.workPhone = clientToSave.workPhone;
    clientDot.mobilePhone = clientToSave.mobilePhone;
    clientDot.charmId = clientToSave.charmId;
  }

  @Override
  public void remove(int clientId) {
    List<ClientDot> clientDots = db.get().clientsStorage;
    for (int i = 0; i < clientDots.size(); i++)
      if (clientDots.get(i).id == clientId) clientDots.remove(i);
  }

  @Override
  public List<ClientRecords> getRecords(ClientFilter clientFilter) {
    List<ClientDot> temp = db.get().clientsStorage;
    List<ClientRecords> clientRecords = new ArrayList<>();

    for (ClientDot clientDot : temp) {
      if (clientFilter.fio != null) {
        if ((clientDot.name.contains(clientFilter.fio))
          || (clientDot.surname.contains(clientFilter.fio))
          || (clientDot.patronymic.contains(clientFilter.fio)))
          clientRecords.add(clientDot.toClientRecords());
      } else {
        clientRecords.add(clientDot.toClientRecords());
      }
    }

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
    List<ClientDot> temp = db.get().clientsStorage;
    List<ClientRecords> clientRecords = new ArrayList<>();

    for(ClientDot clientDot : temp) {
      if (clientFilter != null && clientFilter.fio != null) {
        if ((clientDot.name.contains(clientFilter.fio))
          || (clientDot.surname.contains(clientFilter.fio))
          || (clientDot.patronymic.contains(clientFilter.fio))) {
            clientRecords.add(clientDot.toClientRecords());
        }
      }
      else {
        clientRecords.add(clientDot.toClientRecords());
      }
    }

    return clientRecords.size();
  }

  @Override
  public List<Charm> getCharms() {
    return db.get().charms;
  }

  private ClientDot getClient(int clientId) {

    List<ClientDot> clientDots = db.get().clientsStorage;
    for (ClientDot clientDot : clientDots)
      if (clientDot.id == clientId) return clientDot;
    return null;
  }
}
