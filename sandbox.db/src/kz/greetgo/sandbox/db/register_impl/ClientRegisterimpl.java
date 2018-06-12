package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.List;

public class ClientRegisterimpl implements ClientRegister {
  @Override
  public ClientDetail get(int clientId) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Charm> getCharms() {
    throw new UnsupportedOperationException();
  }
}
