package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.dbmodels.*;
import kz.greetgo.util.RND;

import java.util.*;
import java.util.stream.Collectors;

public class DbModelConverter {


    public Client convertToClient(DbClient dbClient, DbClientPhone[] dbPhones, DbClientAddress dbAddressFactual, DbClientAddress dbAddressRegistered){
        Client client = new Client();
        client.id = dbClient.id;
        client.name = dbClient.name;
        client.surname = dbClient.surname;
        client.patronymic = dbClient.patronymic;
        client.genderType = GenderType.valueOf(dbClient.gender);
        client.charmId = dbClient.charm;
        client.phones = new Phone[dbPhones.length];
        for (int i = 0; i <dbPhones.length ; i++) {
            Phone phone = new Phone();
            DbClientPhone dbClientPhone = dbPhones[i];
            phone.phoneType = PhoneType.valueOf(dbClientPhone.type);
            phone.number = dbClientPhone.number;
            phone.validity = dbClientPhone.validity;
            client.phones[i]=phone;
        }
        client.factualAddress = new Address();
        client.factualAddress.house = dbAddressFactual.house;
        client.factualAddress.flat = dbAddressFactual.flat;
        client.factualAddress.street = dbAddressFactual.street;
        client.registeredAddress = new Address();
        client.registeredAddress.house = dbAddressRegistered.house;
        client.registeredAddress.flat = dbAddressRegistered.flat;
        client.registeredAddress.street = dbAddressRegistered.street;
        client.validity = dbClient.validity;
        client.birthDate = dbClient.birthDate.getTime();
        return client;
    }
    public DbClient convertToDbClient(Client client){
        DbClient dbClient = new DbClient();
        dbClient.id = client.id;
        dbClient.name = client.name;
        dbClient.surname = client.surname;
        dbClient.patronymic = client.patronymic;
        dbClient.gender = client.genderType.toString();
        dbClient.charm = client.charmId;
        dbClient.birthDate = new Date(client.birthDate);
        dbClient.validity = client.validity;
        return dbClient;
    }

    public DbClientAddress convertToDbClientAddressRegistered(Client client){
        DbClientAddress dbClientAddress = new DbClientAddress();
        dbClientAddress.client = client.id;
        dbClientAddress.flat = client.registeredAddress.flat;
        dbClientAddress.house = client.registeredAddress.house;
        dbClientAddress.street = client.registeredAddress.street;
        dbClientAddress.type = AddressType.REG.toString();
        return dbClientAddress;
    }

    public DbClientAddress convertToDbClientAddressFactual(Client client){
        DbClientAddress dbClientAddress = new DbClientAddress();
        dbClientAddress.client = client.id;
        dbClientAddress.flat = client.factualAddress.flat;
        dbClientAddress.house = client.factualAddress.house;
        dbClientAddress.street = client.factualAddress.street;
        dbClientAddress.type = AddressType.FACT.toString();
        return dbClientAddress;
    }


    public DbClientPhone[] convertToDbClientPhones(Client client){
        DbClientPhone[] dbClientPhones = new DbClientPhone[client.phones.length];
        for (int i = 0; i < dbClientPhones.length; i++) {
            DbClientPhone dbClientPhone = new DbClientPhone();
            dbClientPhone.type = client.phones[i].phoneType.toString();
            dbClientPhone.number = client.phones[i].number ;
            dbClientPhone.validity = client.phones[i].validity ;
            dbClientPhone.client = client.id;
            dbClientPhones[i]=dbClientPhone;
        }

        return dbClientPhones;
    }

    public DbClientAccount convertToDbClientAccount(Client client){
        DbClientAccount dbClientAccount = new DbClientAccount();
        dbClientAccount.client= client.id;
        dbClientAccount.number=RND.str(20);
        dbClientAccount.money=0;
        dbClientAccount.registered_at = new Date();
        dbClientAccount.validity=true;
        return  dbClientAccount;
    }

    public ClientRecordsToSend convertToClientRecordsToSend(DbClientRecords[] dbClientRecordss, int size){
        ClientRecordsToSend clientRecordsToSend = new ClientRecordsToSend();
        clientRecordsToSend.size = size;
        clientRecordsToSend.table = (ArrayList<ClientRecord>) Arrays.stream(dbClientRecordss).map(
                dbClientRecords -> {
                    ClientRecord clientRecord = new ClientRecord();
                    clientRecord.id = dbClientRecords.id;
                    clientRecord.fullName = dbClientRecords.fullName;
                    clientRecord.age = dbClientRecords.age.getTime();
                    clientRecord.maxBalance = dbClientRecords.maxBalance;
                    clientRecord.minBalance = dbClientRecords.minBalance;
                    clientRecord.totalBalance = dbClientRecords.totalBalance;
                    return clientRecord;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        return clientRecordsToSend;
    }





}
