package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter);

  void deleteClient(int clientId);

  ClientDetails getClientDetails(int clientId);

  ClientRecord save(ClientToSave editedClient);

  List<Character> getCharacters();

  int getClientCount(ClientRecordFilter clientRecordFilter);
}
