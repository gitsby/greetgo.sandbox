package kz.greetgo.sandbox.stand.stand_register_impls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.RecordClient;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Bean
public class ClientRegisterStand implements ClientRegister {


    private final int sliceNum = 10;
    public BeanGetter<StandDb> db;

    @Override
    public boolean deleteClient(String clientId) {
        if (clientId == null) {
            System.out.println("NULL");
            return false;
        }
        db.get().deleteClient(clientId);
        return true;
    }

    @Override
    public int getPaginationNum() {
        return db.get().clientDotList.size() / sliceNum
                + ((db.get().clientDotList.size() % sliceNum == 0) ? 0 : 1);
    }

    @Override
    public Client getClientById(String clientId) {
        System.out.println("SEARCHING FOR: " + clientId);
        return db.get().getClientById(clientId);
    }

    @Override
    public List<RecordClient> searchClient(String searchName) {
        List<RecordClient> searchClients = new ArrayList<>();
        System.out.println("ToSearch:" + searchName);
        if (searchName == null) {
            return getClientSlice(toRecordList(db.get().clientDotList), "0");
        }
        for (Client client : db.get().clientDotList) {
            if (client.surname.contains(searchName)) {
                RecordClient foundClient = new RecordClient();
                foundClient.id = client.id;
                foundClient.name = client.name;
                foundClient.surname = client.surname;
                foundClient.patronymic = client.patronymic;

                foundClient.age = client.age;
                foundClient.accBalance = client.accBalance;
                foundClient.maxBalance = client.maxBalance;
                searchClients.add(foundClient);
            }
        }
        return searchClients;
    }

    private List<RecordClient> toRecordList(List<Client> clientDotList) {
        List<RecordClient> clients = new ArrayList<>();
        System.out.println("Sorting");
        for (Client client : clientDotList) {
            RecordClient recordClient = new RecordClient();
            recordClient.id = client.id;
            recordClient.name = client.name;
            recordClient.surname = client.surname;
            recordClient.patronymic = client.patronymic;
            recordClient.age = client.age;
            recordClient.character = client.charm.name;

            recordClient.accBalance = client.accBalance;
            recordClient.maxBalance = client.maxBalance;
            clients.add(recordClient);
        }
        return clients;
    }

    public List<RecordClient> getClientSlice(List<RecordClient> recordClients, String paginationPage) {
        List<RecordClient> clients;
        int currentPagination = Integer.parseInt(paginationPage);
        int startSlice = currentPagination * sliceNum;
        int endSlice = sliceNum * currentPagination;
        endSlice += sliceNum;

        if (endSlice > db.get().clientDotList.size()) {
            endSlice = db.get().clientDotList.size();
        }

        clients = recordClients.subList(startSlice, endSlice);

        return clients;
    }

    @Override
    public List<RecordClient> sortClientByColumnNum(String columnNum, String paginationPage) {
        int currentColumn = Integer.parseInt(columnNum);
        List<RecordClient> sortedList = toRecordList(db.get().clientDotList);
        if (currentColumn == 1) {
            Collections.sort(sortedList, Comparator.comparing(o -> o.surname));
        } else if (currentColumn == 3) {
            Collections.sort(sortedList, (o1, o2) -> (o1.age > o2.age) ? 1 : ((o1.age < o2.age) ? -1 : 0));
        } else if (currentColumn == 4) {
            Collections.sort(sortedList, (o1, o2) -> (o1.accBalance > o2.accBalance) ? 1 : ((o1.accBalance < o2.accBalance) ? -1 : 0));
        } else if (currentColumn == 5) {
            Collections.sort(sortedList, (o1, o2) -> (o1.maxBalance > o2.maxBalance) ? 1 : ((o1.maxBalance < o2.maxBalance) ? -1 : 0));
        } else if (currentColumn == 6) {
            Collections.sort(sortedList, (o1, o2) -> (o1.minBalance > o2.minBalance) ? 1 : ((o1.minBalance < o2.minBalance) ? -1 : 0));
        }
        sortedList = getClientSlice(sortedList, paginationPage);
        return sortedList;
    }

    @Override
    public boolean addNewClient(String newClient) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(newClient);
            Client client = mapper.readValue(newClient, Client.class);
            client.id = db.get().clientDotList.get(db.get().clientDotList.size() - 1).id + 1;
            db.get().clientDotList.add(client);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
