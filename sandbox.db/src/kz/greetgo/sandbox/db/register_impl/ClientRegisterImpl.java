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
  public ClientDetail detail(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientInfo get(int clientId) {
    return clientDao.get().get(clientId);
  }

  @Override
  public void save(ClientToSave clientToSave) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ClientRecords> getRecords(ClientFilter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRecordsCount(ClientFilter filter) {
    if (filter == null) return clientDao.get().getClientsCount();
    else if (filter.fio == null) return clientDao.get().getClientsCount();
    return clientDao.get().getClientsCountWithFilter(filter.fio);
  }

  @Override
  public List<Charm> getCharms() {
    throw new UnsupportedOperationException();
  }


}
