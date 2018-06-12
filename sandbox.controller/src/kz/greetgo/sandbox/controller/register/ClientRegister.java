package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.RecordClient;

import java.util.List;

public interface ClientRegister {
    //
    // FIXME: 6/12/18 Создай отдельный класс для фильтра
    List<RecordClient> getClients(String columnNum,
                                             String paginationPage,
                                             String searchText,
                                             int sliceNum);

    // FIXME: 6/12/18 pochemu clientId - String?
    boolean deleteClient(String clientId);

    ClientDetails getClientById(int clientId);

    // FIXME: 6/12/18 zachem return int?
    int editedClient(ClientToSave editedClient);

    List<Character> getCharacters();

    // FIXME: 6/12/18 вытаскивай количество клиентов и высчитывай пагинацию на клиенте
    int getRequestedPaginationNum(String searchText, int sliceNum);
}
