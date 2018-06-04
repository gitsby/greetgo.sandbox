package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.RecordClient;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientCotroller implements Controller {

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
    public boolean editedClient(@Par("editedClient") String editedClient) {
        System.out.println("EDITED Client:" + editedClient);
        return false;
    }

    @ToJson
    @Mapping("/search")
    public List<RecordClient> searchClient(@Par("searchName") String searchName) {
        return clientRegister.get().searchClient(searchName);
    }

    @ToJson
    @Mapping("/add_client")
    public boolean addClient(@Par("newClient") String newClient) {
        return clientRegister.get().addNewClient(newClient);
    }

    @ToJson
    @Mapping("/sort")
    public List<RecordClient> sortClientByColumnNum(@Par("columnNum") String columnNum,
                                                    @Par("paginationPage") String paginationPage) {
        return clientRegister.get().sortClientByColumnNum(columnNum, paginationPage);
    }

    @ToJson
    @Mapping("/pagination_page_num")
    public int getPaginationNum() {
        return clientRegister.get().getPaginationNum();
    }

    @ToJson
    @Mapping("/getClientWithId")
    public Client getClientById(@Par("clientId") String clientId) {
        System.out.println("Retrieving from Controller: " + clientId);
        return clientRegister.get().getClientById(clientId);
    }


}
