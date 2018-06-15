package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter);

  void deleteClient(int clientId);

  ClientDetails details(int clientId);

  ClientRecord save(ClientToSave editedClient);

  List<CharmRecord> charm();

  int getClientCount(ClientRecordFilter clientRecordFilter);
}
