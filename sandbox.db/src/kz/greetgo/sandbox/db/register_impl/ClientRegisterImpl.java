package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;

  @Override
  public List<ClientRecord> getClients(String columnNum, String paginationPage, String searchText, int sliceNum) {
    throw new UnsupportedOperationException();L
  }

  @Override
  public boolean deleteClient(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientDetails getClientById(int clientId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int editedClient(ClientToSave editedClient) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Character> getCharacters() {
    SQL sql = new SQL();
    sql.SELECT("");
    List<Character> characters = null;
    return characters;
  }

  @Override
  public int getRequestedPaginationNum(String searchText, int sliceNum) {
    throw new UnsupportedOperationException();
  }
}
