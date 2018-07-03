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
    public TableToSend getTableData(int skipNumber, int limit, String sortDirection, String sortType){
        return null;
    }


    @Override
    public User getExactUser(int userID){
        boolean existence = tableDao.get().countClientsWithUserID( userID) > 0;
//        System.err.println(existence);
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

        if(tableDao.get().countClientsWithUserID( user.id) <= 0){
            return -1;
        }

        if(!checkForContraints(user))
            return -1;
        System.err.println(user.toString());

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


        DbClientPhone[] dbClientPhonesLoaded = tableDao.get().getPhones(user.id);
        DbClientPhone[]  dbClientPhones = dbModelConverter.convertToDbClientPhones(user);

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
    public String deleteUser(int userID){
        if(tableDao.get().countClientsWithUserID(userID) <= 0){
            return "-1";
        }
        tableDao.get().deleteClient(userID);
        tableDao.get().deletePhone(userID);
        tableDao.get().deleteAccount(userID);
        return "1";
    }

}


