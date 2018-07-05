package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.DbCharm;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClient;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientAddress;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientPhone;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.db.dao.TableDao;

import java.util.ArrayList;
import java.util.Arrays;


@Bean
public class TableRegisterImpl implements TableRegister{

    public BeanGetter<TableDao> tableDao;

    public DbModelConverter dbModelConverter = new DbModelConverter();

    @Override
    public TableToSend getTableData(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText){

        TableToSend tableToSend = new TableToSend();

        sortDirection = sortDirection.toUpperCase();
        filterType = filterType.toUpperCase();
        sortType = sortType.toUpperCase();
        filterText = "%"+ filterText + "%";
        if (checkParams(skipNumber,limit,sortDirection,sortType,filterType,filterText)){
            tableToSend.table.add(new TableModel());
            return tableToSend;
        }

        if(sortType.equals("FULLNAME")){
            tableToSend.table= sortDirection.equals("DESC")?
                    tableDao.get().getFullNameDesc(skipNumber, limit, filterType, filterText):
                    tableDao.get().getFullNameAsc(skipNumber, limit, filterType, filterText);
        }
        if(sortType.equals("AGE")){
            tableToSend.table= sortDirection.equals("DESC")?
                    tableDao.get().getAgeDesc(skipNumber, limit, filterType, filterText):
                    tableDao.get().getAgeAsc(skipNumber, limit, filterType, filterText);
        }
        if(sortType.equals("MINBALANCE")){
            tableToSend.table= sortDirection.equals("DESC")?
                    tableDao.get().getMinBalanceDesc(skipNumber, limit, filterType, filterText):
                    tableDao.get().getMinBalanceAsc(skipNumber, limit, filterType, filterText);
        }
        if(sortType.equals("MAXBALANCE")){
            tableToSend.table= sortDirection.equals("DESC")?
                    tableDao.get().getMaxBalanceDesc(skipNumber, limit, filterType, filterText):
                    tableDao.get().getMaxBalanceAsc(skipNumber, limit, filterType, filterText);
        }
        if(sortType.equals("TOTALBALANCE")){
            tableToSend.table= sortDirection.equals("DESC")?
                    tableDao.get().getTotalBalanceDesc(skipNumber, limit, filterType, filterText):
                    tableDao.get().getTotalBalanceAsc(skipNumber, limit, filterType, filterText);
        }

        tableToSend.size=tableDao.get().getTableSize();
        return tableToSend;
    }

    public Boolean checkParams(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText){

        return skipNumber==-1 || limit==-1 ||
                sortDirection.isEmpty() ||sortType.isEmpty() ||
                filterText.isEmpty() || filterType.isEmpty() ||
                skipNumber==null ||  limit==null ||
                sortDirection==null || sortType==null ||
                filterText==null || filterType==null ||
                !sortDirection.matches("(ASC|DESC)") ||
                !sortType.matches("(FULLNAME|AGE|MAXBALANCE|MINBALANCE|TOTALBALANCE)") ||
                !filterType.matches("NAME|SURNAME|PATRONYMIC");

    }


    @Override
    public User getExactUser(Integer userID){
        boolean existence = tableDao.get().countClientsWithUserID( userID) > 0;
        if(!existence){
            User user = new User();
            user.id = -1;
            return user;
        }

        DbClient dbClient = tableDao.get().getExactClient(userID);
        System.err.println(dbClient.toString());
        DbCharm  dbCharm = tableDao.get().getCharm(1);
        System.err.println(dbCharm.toString());

        DbClientPhone[] dbClientPhones = tableDao.get().getPhones(userID);
        if(dbClientPhones!=null)
            for (DbClientPhone dbClientPhone:dbClientPhones){
                System.err.println(dbClientPhone.toString());
            }

        DbClientAddress dbClientAddressFactual = tableDao.get().getClientAddress(userID, AddressType.FACT.toString());
        System.err.println(dbClientAddressFactual.toString());

        DbClientAddress dbClientAddressRegistered = tableDao.get().getClientAddress(userID, AddressType.REG.toString());
        System.err.println(dbClientAddressRegistered.toString());

        User user = dbModelConverter.convertToUser(dbClient,dbCharm,dbClientPhones,dbClientAddressFactual,dbClientAddressRegistered);
        return user;
    }


    private Boolean checkForContraints(User user){
        if (    user.name.isEmpty() || user.name==null ||
                user.surname.isEmpty() || user.surname==null ||
                user.charm==null || user.genderType==null ||
                user.phones==null || user.registeredAddress==null ||
                user.registeredAddress.street.isEmpty() || user.registeredAddress.street == null ||
                user.registeredAddress.flat.isEmpty() || user.registeredAddress.flat == null ||
                user.registeredAddress.house.isEmpty() || user.registeredAddress.house == null){
            return false;
        }
        boolean va=true;
        boolean mob=false;
        for(Phone phone: user.phones){
            if(phone.number.matches("^(\\d{11})?$")){

                va=va&&true;
                if(phone.phoneType==PhoneType.MOBILE ) {
                    mob=true;
                }
            }else {
                va=false;
            }
        }
        return va&&mob;
    }

    @Override
    public Integer createUser(User user){
//
//        if(tableDao.get().countClientsWithUserID( user.id) <= 0){
//            return -2;
//        }

        if(!checkForContraints(user))
            return -1;

        user.validity=true;


        Integer charmId = charmCheck(user.charm);

        DbClient dbClient = dbModelConverter.convertToDbClient(user,charmId);

        tableDao.get().insertClient(dbClient);
        user.id = tableDao.get().getLastClientID();

        DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(user);

        for (DbClientPhone dbClientPhone: dbClientPhones) {
            tableDao.get().insertPhone(dbClientPhone);
        }
        tableDao.get().insertAddress(dbModelConverter.convertToDbClientAddressRegistered(user));
        tableDao.get().insertAddress(dbModelConverter.convertToDbClientAddressFactual(user));

        tableDao.get().insertAccount(dbModelConverter.convertToDbClientAccount(user));
        tableDao.get().insertAccount(dbModelConverter.convertToDbClientAccount(user));
        return user.id;
    }

    public int charmCheck(String charm){
        Integer charmId = tableDao.get().getCharmId(charm);

        if(charmId == null){
            DbCharm dbCharm =  new DbCharm();
            dbCharm.id=0;
            dbCharm.name = charm;
            dbCharm.description = charm;
            dbCharm.energy = 255.0f;
            tableDao.get().insertCharm(dbCharm);
            charmId = tableDao.get().getCharmId(charm);
        }
        return charmId;

    }

    @Override
    public String changeUser(User user){


        if(tableDao.get().countClientsWithUserID( user.id) <= 0){
            return "-1";
        }

        if(!checkForContraints(user))
            return "-1";

        user.validity=true;

        DbClientPhone[] dbClientPhonesLoaded = tableDao.get().getPhones(user.id);
        DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(user);

        for (int i = 0; i <dbClientPhones.length; i++) {
            for (int j = 0; j <dbClientPhonesLoaded.length ; j++) {
                if(dbClientPhones[i].number.equals(dbClientPhonesLoaded[j].number) && dbClientPhones[i].validity!=dbClientPhonesLoaded[i].validity){
                    tableDao.get().updatePhone(dbClientPhones[i]);
                    dbClientPhones[i]=new DbClientPhone();
                    dbClientPhones[i].number="0";
                }

            }
        }
        for (DbClientPhone dbClientPhone : dbClientPhones) {
            if (!dbClientPhone.number.equals("0"))
                tableDao.get().insertPhone(dbClientPhone);
        }

        Integer charmId = charmCheck(user.charm);

        tableDao.get().updateClient(dbModelConverter.convertToDbClient(user, charmId));
        tableDao.get().updateAddress(dbModelConverter.convertToDbClientAddressRegistered(user));
        tableDao.get().updateAddress(dbModelConverter.convertToDbClientAddressFactual(user));

        return "1";
    }

    @Override
    public String deleteUser(Integer userID){

        if(tableDao.get().countClientsWithUserID(userID) <= 0 || userID==null){
            return "-1";
        }
        tableDao.get().deleteClient(userID);
        tableDao.get().deletePhone(userID);
        tableDao.get().deleteAccount(userID);
        return "1";
    }

}


