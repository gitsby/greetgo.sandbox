package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.RecordClient;

import java.util.List;

public interface ClientRegister {
    //
    List<RecordClient> getClients(String columnNum,
                                             String paginationPage,
                                             String searchText,
                                             int sliceNum);

    boolean deleteClient(String clientId);

    ClientDetails getClientById(int clientId);

    int editedClient(ClientToSave editedClient);

    List<Character> getCharacters();

    int getRequestedPaginationNum(String searchText, int sliceNum);
}
