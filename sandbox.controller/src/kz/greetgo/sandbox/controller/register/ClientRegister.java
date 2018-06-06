package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.EditClient;

import java.util.List;

public interface ClientRegister {

    List<ClientRecord> searchClient(String searchName);

    List<ClientRecord> sortClientByColumnNum(String columnNum, String paginationPage, String searchText);

    boolean deleteClient(String clientId);

    ClientToSave getClientById(int clientId);

    boolean editedClient(EditClient editedClient);

    List<Character> getCharacters();

    int getRequestedPaginationNum(String searchText);
}
