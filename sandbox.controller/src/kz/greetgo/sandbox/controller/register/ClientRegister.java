package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClients(ClientRecordPhilter clientRecordPhilter);

  void deleteClient(int clientId);

  ClientDetails getClientById(int clientId);

  ClientRecord editedClient(ClientToSave editedClient);

  List<Character> getCharacters();

  int getRequestedPaginationNum(ClientRecordPhilter clientRecordPhilter);
}
