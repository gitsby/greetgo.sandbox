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
    throw new UnsupportedOperationException();
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
