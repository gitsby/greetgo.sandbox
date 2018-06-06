package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.EditClient;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @Mapping("/delete")
    public boolean deleteClient(@Par("index") String index) {
        System.out.println("Value: " + index);
        clientRegister.get().deleteClient(index);
        return false;
    }

    @ToJson
    @Mapping("/edit")
    public boolean editedClient(@Json @Par("editedClient") EditClient editedClient) {
        System.out.println("EDITED Client:" + editedClient);
        return clientRegister.get().editedClient(editedClient);
    }

    @ToJson
    @Mapping("getPaginationNum")
    public int getPaginationNum(@Par("searchText") String){
        return 0;
    }

    @ToJson
    @Mapping("/getClients")
    public List<ClientRecord> getClients(@Par("columnName") String columnName,
                                         @Par("paginationPage") String paginationPage,
                                         @Par("searchName") String searchName) {
        return clientRegister.get().sortClientByColumnNum(columnName, paginationPage, searchName);
    }

    @ToJson
    @Mapping("/getClientWithId")
    public ClientToSave getClientById(@Par("clientId") String clientId) {
        System.out.println("Retrieving from Controller: " + clientId);
        return clientRegister.get().getClientById(Integer.parseInt(clientId));
    }

    @ToJson
    @Mapping("/characters")
    public List<Character> getCharacters(){
        return clientRegister.get().getCharacters();
    }

}
