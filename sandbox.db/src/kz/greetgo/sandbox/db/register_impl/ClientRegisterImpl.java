package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.EditClient;
import kz.greetgo.sandbox.controller.model.RecordClient;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  @Override
  public List<RecordClient> getClients(String columnNum, String paginationPage, String searchText, int sliceNum) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteClient(String clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientToSave getClientById(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int editedClient(EditClient editedClient) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Character> getCharacters() {
    return null;
  }

  @Override
  public int getRequestedPaginationNum(String searchText, int sliceNum) {
    throw new UnsupportedOperationException();
  }
}
