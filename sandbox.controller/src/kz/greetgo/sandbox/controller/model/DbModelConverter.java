package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.model.dbmodels.*;
import kz.greetgo.util.RND;
import sun.security.pkcs11.Secmod;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class DbModelConverter {


    public User convertToUser(DbClient dbUser, DbCharm dbCharm, DbClientPhone[] dbPhones, DbClientAddress dbAddressFactual, DbClientAddress dbAddressRegistered){
        User user = new User();
        user.id = dbUser.id;
        user.name = dbUser.name;
        user.surname = dbUser.surname;
        user.patronymic = dbUser.patronymic;
        user.genderType = GenderType.valueOf(dbUser.gender);
        user.charm = dbCharm.name;
        user.phones = new Phone[dbPhones.length];
        for (int i = 0; i <dbPhones.length ; i++) {
            Phone phone = new Phone();
            DbClientPhone dbClientPhone = dbPhones[i];
            phone.phoneType = PhoneType.valueOf(dbClientPhone.type);
            phone.number = dbClientPhone.number;
            phone.validity = dbClientPhone.validity;
            user.phones[i]=phone;
        }
        System.err.print(dbAddressFactual + "\n" + dbAddressRegistered);
        user.factualAddress = new Address();
        user.factualAddress.house = dbAddressFactual.house;
        user.factualAddress.flat = dbAddressFactual.flat;
        user.factualAddress.street = dbAddressFactual.street;
        user.registeredAddress = new Address();
        user.registeredAddress.house = dbAddressRegistered.house;
        user.registeredAddress.flat = dbAddressRegistered.flat;
        user.registeredAddress.street = dbAddressRegistered.street;
        user.validity = dbUser.validity;
        user.birthDate = dbUser.birthDate.getTime();
        return user;
    }
    public DbClient convertToDbClient(User user, int charmId){
        DbClient dbClient = new DbClient();
        dbClient.id = user.id;
        dbClient.name = user.name;
        dbClient.surname = user.surname;
        dbClient.patronymic = user.patronymic;
        dbClient.gender = user.genderType.toString();
        dbClient.charm = charmId;
        dbClient.birthDate = new Date(user.birthDate);
        dbClient.validity = user.validity;
        return dbClient;
    }

    public DbClientAddress convertToDbClientAddressRegistered(User user){
        DbClientAddress dbClientAddress = new DbClientAddress();
        dbClientAddress.client = user.id;
        dbClientAddress.flat = user.registeredAddress.flat;
        dbClientAddress.house = user.registeredAddress.house;
        dbClientAddress.street = user.registeredAddress.street;
        dbClientAddress.type = AddressType.REG.toString();
        return dbClientAddress;
    }

    public DbClientAddress convertToDbClientAddressFactual(User user){
        DbClientAddress dbClientAddress = new DbClientAddress();
        dbClientAddress.client = user.id;
        dbClientAddress.flat = user.factualAddress.flat;
        dbClientAddress.house = user.factualAddress.house;
        dbClientAddress.street = user.factualAddress.street;
        dbClientAddress.type = AddressType.FACT.toString();
        return dbClientAddress;
    }


    public DbClientPhone[] convertToDbClientPhones(User user){
        DbClientPhone[] dbClientPhones = new DbClientPhone[user.phones.length];
        for (int i = 0; i < dbClientPhones.length; i++) {
            DbClientPhone dbClientPhone = new DbClientPhone();
            dbClientPhone.type = user.phones[i].phoneType.toString();
            dbClientPhone.number = user.phones[i].number ;
            dbClientPhone.validity = user.phones[i].validity ;
            dbClientPhone.client = user.id;
            dbClientPhones[i]=dbClientPhone;
        }

        return dbClientPhones;
    }

    public DbClientAccount convertToDbClientAccount(User user){
        DbClientAccount dbClientAccount = new DbClientAccount();
        dbClientAccount.client=user.id;
        dbClientAccount.number=RND.str(20);
        dbClientAccount.money=0;
        dbClientAccount.registered_at = new Date();
        dbClientAccount.validity=true;
        return  dbClientAccount;
    }

    public TableToSend convertToTableToSend(DbTableModel[] dbTableModels, int size){
        TableToSend tableToSend = new TableToSend();
        tableToSend.size = size;
        tableToSend.table = (ArrayList<TableModel>) Arrays.stream(dbTableModels).map(
                dbTableModel -> {
                    TableModel tableModel = new TableModel();
                    tableModel.id = dbTableModel.id;
                    tableModel.fullName = dbTableModel.fullName;
                    tableModel.age = dbTableModel.age.getTime();
                    tableModel.maxBalance = dbTableModel.maxBalance;
                    tableModel.minBalance = dbTableModel.minBalance;
                    tableModel.totalBalance = dbTableModel.totalBalance;
                    return tableModel;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        return tableToSend;
    }





}
