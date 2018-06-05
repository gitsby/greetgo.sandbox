package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.EditClient;
import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.List;

public interface ClientRegister {

    List<ClientRecord> searchClient(String searchName);

    List<ClientRecord> sortClientByColumnNum(String columnNum, String paginationPage, String searchText);

    boolean deleteClient(String clientId);

    int getPaginationNum();

    ClientToSave getClientById(int clientId);

    boolean editedClient(EditClient editedClient);
}
