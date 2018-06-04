package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.RecordClient;

import java.util.List;

public interface ClientRegister {


    List<RecordClient> searchClient(String searchName);

    List<RecordClient> sortClientByColumnNum(String columnNum, String paginationPage);

    boolean addNewClient(String newClient);

    boolean deleteClient(String clientId);

    int getPaginationNum();

    Client getClientById(String clientId);
}
