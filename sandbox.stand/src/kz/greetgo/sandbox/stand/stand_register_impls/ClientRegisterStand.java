package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

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
        // TODO: 6/4/2018
        db.get().getClientDot().removeIf(client -> clientId.equals(client.id + ""));
        return true;
    }

    @Override
    public int getPaginationNum() {
        return db.get().getClientDot().size() / sliceNum
                + ((db.get().getClientDot().size() % sliceNum == 0) ? 0 : 1);
    }

    @Override
    public ClientToSave getClientById(int clientId) {
        System.out.println("SEARCHING FOR: " + clientId);
        for (ClientDot clientDot : db.get().getClientDot()) {
            if (clientDot.id == clientId) {
                ClientToSave foundClient = new ClientToSave();
                foundClient.id = clientDot.id;
                foundClient.name = clientDot.name;
                foundClient.surname = clientDot.surname;
                foundClient.gender = clientDot.gender;
                foundClient.patronymic = clientDot.patronymic;
                foundClient.birthDate = clientDot.birthDate;

                for (CharacterDot characterDot : db.get().characters) {
                    if (characterDot.id == clientDot.charm) {
                        Character character = new Character();
                        character.name = character.name;
                        foundClient.charm = character;
                    }
                }
                List<PhoneDot> phoneDotList = db.get().getPhoneDotsWithId(clientId);
                List<Phone> phones = new ArrayList<>();
                for (PhoneDot phoneDot : phoneDotList) {
                    Phone phone = new Phone();
                    phone.number = phoneDot.number;
                    phone.client = phoneDot.client;
                    phone.type = phoneDot.type;
                    phones.add(phone);
                }
                Phone[] phonesArr = phones.toArray(new Phone[phoneDotList.size()]);
                foundClient.phones = phonesArr;

                List<AddressDot> addressDotList = db.get().getAddressDotsWithId(clientId);
                List<Address> addresses = new ArrayList<>();
                for (AddressDot addressDot : addressDotList) {
                    Address address = new Address();
                    address.id = addressDot.id;
                    address.clientId = addressDot.clientId;
                    address.house = addressDot.house;
                    address.flat = addressDot.flat;
                    address.street = addressDot.street;
                    address.type = addressDot.type;
                    addresses.add(address);
                }

                Address[] addressDots = addresses.toArray(new Address[addresses.size()]);
                foundClient.addresses = addressDots;
                return foundClient;
            }
        }
        return null;
    }

    @Override
    public boolean editedClient(EditClient editedClient) {
        if (editedClient.id == null) {
            createNewClient(editedClient);
            return true;
        }
        if (editedClient.name != null) {
            if (!editedClient.name.equals(db.get().getClientDot().get(editedClient.id).name)) {
                db.get().getClientDot().get(editedClient.id).name = editedClient.name;
            }
        }
        if (editedClient.surname != null) {
            if (!editedClient.surname.equals(db.get().getClientDot().get(editedClient.id).surname)) {
                db.get().getClientDot().get(editedClient.id).surname = editedClient.surname;
            }
        }
        if (editedClient.patronymic != null) {
            if (!editedClient.patronymic.equals
                    (db.get().getClientDot().get(editedClient.id).patronymic)) {
                db.get().getClientDot().get(editedClient.id).patronymic = editedClient.patronymic;
            }
        }
        if (editedClient.birthDate != null) {
            if (!editedClient.birthDate.equals
                    (db.get().getClientDot().get(editedClient.id).birthDate)) {
                db.get().getClientDot().get(editedClient.id).birthDate = editedClient.birthDate;
            }
        }
        if (editedClient.gender != null) {
            System.out.println("GENDER: " + editedClient.gender);
            if (!editedClient.gender.equals
                    (db.get().getClientDot().get(editedClient.id).gender)) {
                db.get().getClientDot().get(editedClient.id).gender = editedClient.gender;
            }
        }

        if (editedClient.addedAddresses != null) {
            // Added
            for (Address address : editedClient.addedAddresses) {
                AddressDot addressDot = new AddressDot();
                addressDot.id = db.get().getAddressDots().size();
                addressDot.type = address.type;
                addressDot.flat = address.flat;
                addressDot.street = address.street;
                addressDot.house = address.house;
                db.get().getAddressDots().add(addressDot);
            }
        }
        if (editedClient.editedAddresses != null) {
            // Edited
            for (Address address : editedClient.editedAddresses) {
                AddressDot editedAddress = db.get().getAddressDots().get(address.id);
                editedAddress.house = address.house;
                editedAddress.street = address.street;
                editedAddress.flat = address.flat;

            }
        }

        if (editedClient.deletedAddresses != null) {
            // Delete
            for (Address address : editedClient.deletedAddresses) {
                for (AddressDot addressDot : db.get().getAddressDotsWithId(address.clientId)) {
                    if (address.id == addressDot.id) {
                        db.get().getAddressDots().remove(addressDot);
                        break;
                    }
                }
            }
        }

        if (editedClient.addedPhones != null) {
            for (Phone phone : editedClient.addedPhones) {
                PhoneDot phoneDot = new PhoneDot();
                phoneDot.type = phone.type;
                phoneDot.client = phone.client;
                phoneDot.number = phone.number;
                db.get().getPhoneDots().add(phoneDot);
            }
        }
        if (editedClient.editedPhones != null) {

        }
        if (editedClient.deletedPhones != null) {
            System.out.println("DELETED PHONES NOT NULL");
            for (Phone phone : editedClient.deletedPhones) {
                for (PhoneDot phoneDot : db.get().getPhoneDots()) {
                    if (phone.number.equals(phoneDot.number)) {
                        System.out.println("REMOVING: " + phone.number);
                        db.get().getPhoneDots().remove(phoneDot);
                        break;
                    }
                }
            }
        }
        return true;
    }

    private void createNewClient(EditClient client) {
        client.id = db.get().getClientDot().get(db.get().getClientDot().size() - 1).id + 1;
        ClientDot clientDot = new ClientDot();
        clientDot.id = client.id;
        clientDot.name = client.name;
        clientDot.surname = client.surname;
        clientDot.patronymic = client.patronymic;
        clientDot.birthDate = client.birthDate;
        clientDot.gender = client.gender;
        System.out.println("BEFORE SIZE" + db.get().getClientDot().size());
        db.get().getClientDot().add(clientDot);
        System.out.println("After SIZE" + db.get().getClientDot().size());
        for (Address address : client.addedAddresses) {
            AddressDot addressDot = new AddressDot();
            addressDot.clientId = client.id;
            addressDot.id = db.get().getAddressDots().get(db.get().getAddressDots().size() - 1).id + 1;
            addressDot.house = address.house;
            addressDot.street = address.street;
            addressDot.flat = address.flat;
            addressDot.type = address.type;
            db.get().getAddressDots().add(addressDot);
        }
        for (Phone phone : client.addedPhones) {
            PhoneDot phoneDot = new PhoneDot();
            phoneDot.client = client.id;
            phoneDot.number = phone.number;
            phoneDot.type = phone.type;
            db.get().getPhoneDots().add(phoneDot);
        }
        ClientAccountDot clientAccountDot = new ClientAccountDot();
        clientAccountDot.id = db.get().getClientAccountDots().get(db.get().getClientAccountDots().size() - 1).id + 1;
        clientAccountDot.money = 456;
        db.get().getClientAccountDots().add(clientAccountDot);
    }


    @Override
    public List<ClientRecord> searchClient(String searchName) {
        List<ClientRecord> searchClients = new ArrayList<>();
        System.out.println("ToSearch:" + searchName);
        if (searchName == null) {
            return getClientSlice(createRecordList(), "0");
        }
        for (ClientDot client : db.get().getClientDot()) {
            String snmn = client.surname + " " + client.name + " " + client.patronymic;
            if (snmn.contains(searchName)) {
                ClientRecord foundClient = new ClientRecord();
                foundClient.id = client.id;
                foundClient.name = client.name;
                foundClient.surname = client.surname;
                foundClient.patronymic = client.patronymic;
                foundClient.character = "char";
                foundClient.age = 60;
                foundClient.accBalance = (int) db.get().getClientAccountDots().get(client.id).money;
                foundClient.minBalance = (int) db.get().getClientAccountDots().get(client.id).money;
                foundClient.maxBalance = (int) db.get().getClientAccountDots().get(client.id).money;
                searchClients.add(foundClient);
            }
        }
        for (ClientRecord clientRecord : searchClients) {
            clientRecord.paginationNum = getPaginationNum(searchClients);
        }
        return searchClients;
    }

    public List<ClientRecord> getClientSlice(List<ClientRecord> clientRecords, String paginationPage) {
        List<ClientRecord> clients;
        int currentPagination = Integer.parseInt(paginationPage);
        int startSlice = currentPagination * sliceNum;
        int endSlice = sliceNum * currentPagination;
        endSlice += sliceNum;

        if (endSlice > clientRecords.size()) {
            endSlice = clientRecords.size();
        }


        clients = clientRecords.subList(startSlice, endSlice);


        return clients;
    }

    @Override
    public List<ClientRecord> sortClientByColumnNum(String columnName, String paginationPage,
                                                    String searchText) {
//        int currentColumn = Integer.parseInt(columnNum);
        List<ClientRecord> sortedList = searchClient(searchText);
        System.out.println("SEARCHING FOR : " + searchText);
        System.out.println("SORTING: " + columnName);
//        System.out.println("Sorting with: " + currentColumn);
        if (columnName.equals("surname")) {
            Collections.sort(sortedList, Comparator.comparing(o -> o.surname));
        } else if (columnName.equals("age")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.age > o2.age) ? 1 : ((o1.age < o2.age) ? -1 : 0));
        } else if (columnName.equals("total")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.accBalance > o2.accBalance) ? 1 : ((o1.accBalance < o2.accBalance) ? -1 : 0));
        } else if (columnName.equals("max")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.maxBalance > o2.maxBalance) ? 1 : ((o1.maxBalance < o2.maxBalance) ? -1 : 0));
        } else if (columnName.equals("min")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.minBalance > o2.minBalance) ? 1 : ((o1.minBalance < o2.minBalance) ? -1 : 0));
        } else if (columnName.equals("-surname")) {
            Collections.sort(sortedList, (o1, o2) -> (-o1.surname.compareTo(o2.surname)));
        } else if (columnName.equals("-age")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.age < o2.age) ? 1 : ((o1.age > o2.age) ? -1 : 0));
        } else if (columnName.equals("-total")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.accBalance < o2.accBalance) ? 1 : ((o1.accBalance > o2.accBalance) ? -1 : 0));
        } else if (columnName.equals("-max")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.maxBalance < o2.maxBalance) ? 1 : ((o1.maxBalance > o2.maxBalance) ? -1 : 0));
        } else if (columnName.equals("-min")) {
            Collections.sort(sortedList, (o1, o2) -> (o1.minBalance < o2.minBalance) ? 1 : ((o1.minBalance > o2.minBalance) ? -1 : 0));
        }
        sortedList = getClientSlice(sortedList, paginationPage);

        return sortedList;
    }

    public int getPaginationNum(List<ClientRecord> list) {
        return list.size() / sliceNum
                + ((list.size() % sliceNum == 0) ? 0 : 1);
    }

    public List<ClientRecord> createRecordList() {
        List<ClientRecord> clientRecords = new ArrayList<>();
        for (ClientDot clientDot : db.get().getClientDot()) {
            ClientRecord clientRecord = new ClientRecord();
            clientRecord.id = clientDot.id;
            clientRecord.name = clientDot.name;
            clientRecord.surname = clientDot.surname;
            clientRecord.patronymic = clientDot.patronymic;
            clientRecord.age = 65;
            for (CharacterDot characterDot : db.get().characters) {
                if (characterDot.id == clientDot.charm) {
                    clientRecord.character = characterDot.name;
                }
            }
            clientRecord.minBalance = (int) db.get().getClientAccountDots().get(clientRecord.id).money;
            clientRecord.maxBalance = (int) db.get().getClientAccountDots().get(clientRecord.id).money;
            clientRecords.add(clientRecord);
        }
        return clientRecords;
    }


}
