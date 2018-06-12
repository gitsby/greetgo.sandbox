package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClients(ClientRecordPhilter clientRecordPhilter);

  boolean deleteClient(int clientId);

  ClientDetails getClientById(int clientId);

  // Возвращает id добавленого клиента для того чтобы потом добавить его в список клиентов
  // для возможного следующего редактирования
  int editedClient(ClientToSave editedClient);

  List<Character> getCharacters();

  int getRequestedPaginationNum(ClientRecordPhilter clientRecordPhilter);
}
