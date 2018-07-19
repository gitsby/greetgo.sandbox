package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.*;

import java.io.OutputStream;
import java.util.List;

public interface ClientRegister {

  List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter);

  void deleteClient(int clientId);

  ClientDetails details(int clientId);

  ClientRecord save(ClientToSave editedClient);

  List<CharmRecord> getCharms();

  int getClientCount(ClientRecordFilter clientRecordFilter);
}
