package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.stand.beans.StandJsonDb;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Bean
public class TableRegisterStand implements TableRegister {

    public BeanGetter<StandJsonDb> db;

    public enum SortType{
        FULLNAME,
        CHARM,
        AGE,
        TOTALBALANCE,
        MAXBALANCE,
        MINBALANCE,;

    }

    @Override
    public TableToSend getTableData(int skipNumber, int limit, String sortDirection, String sortType){
        Table queriedTable  = new Table();
        queriedTable.data=db.get().table.data.stream().sorted(((o1, o2) -> {
            SortType enumSortType = SortType.valueOf(sortType.toUpperCase());
            switch (enumSortType) {
                case FULLNAME:
                    return "desc".equals(sortDirection)?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
                case CHARM:
                    return "desc".equals(sortDirection)?-o1.charm.compareTo(o2.charm):o1.charm.compareTo(o2.charm);
                case AGE:
                    return "desc".equals(sortDirection)?-Long.compare(o1.age,o2.age):Long.compare(o1.age,o2.age);
                case TOTALBALANCE:
                    return "desc".equals(sortDirection)?-Double.compare(o1.totalBalance,o2.totalBalance):Double.compare(o1.totalBalance,o2.totalBalance);
                case MAXBALANCE:
                    return "desc".equals(sortDirection)?-Double.compare(o1.maxBalance,o2.maxBalance):Double.compare(o1.maxBalance,o2.maxBalance);
                case MINBALANCE:
                    return "desc".equals(sortDirection)?-Double.compare(o1.minBalance,o2.minBalance):Double.compare(o1.minBalance,o2.minBalance);
                default:
                    return "desc".equals(sortDirection)?-o1.id.compareTo(o2.id):o1.id.compareTo(o2.id);
            }
        })).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        TableToSend table = new TableToSend();
        table.table=queriedTable.data;
        table.size=tableSize();
        return table;
    }

    @Override
    public int tableSize(){
        try {
            return db.get().users.data.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public String getLastId(){
        try {
            return db.get().lastId;
        }catch(Exception e){
            e.printStackTrace();
            return "-1";
        }
    }


    @Override
    public User getExactUser(String userID){
        try {
            return db.get().users.data.stream().filter((user) -> userID.equals(user.id)).findFirst().get();
        } catch (Exception e){
            e.printStackTrace();
            return new User();
        }
    }

    @Override
    public Boolean checkIfThereUser(String userID){
        try {
            return db.get().users.data.stream().anyMatch((user) -> userID.equals(user.id));
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String createUser(User user){
        if(!checkForValidity(user)){
            return "User is not valid!";
        }
        user.id = Integer.toString(Integer.parseInt(db.get().lastId)+1);
        db.get().users.data.add(user);
        Account account = new Account();
        account.registeredAt = System.currentTimeMillis();
        account.id = db.get().accounts.data.size();
        account.userID = user.id;
        account.moneyNumber=0;
        db.get().accounts.data.add(account);
        db.get().updateDB();
        return getLastId();
    }


    private Boolean checkForValidity(User user){

        if ("".equals(user.name) ||"".equals(user.surname)){
            return false;
        }

        if (user.charm==null || user.genderType==null
                || user.name==null || user.surname==null
                || user.phones==null || user.registeredAddress==null){
            return false;
        }

        if("".equals(user.registeredAddress.street)||"".equals(user.registeredAddress.flat)||"".equals(user.registeredAddress.house)){
            return  false;
        }
        boolean val=false;
        for(Phone phone: user.phones){
            if(phone.phoneType==PhoneType.MOBILE && phone.number.matches("^(\\d{11})?$")){
                val=true;
            }else if(phone.number.matches("^(\\d{11})?$")){
                val=true;
            }
        }

        return val;
    }

    @Override
    public String changeUser(User user){
        if (!checkForValidity(user)){
            return "User is not valid!";
        }
        db.get().users.data.removeIf(user1 -> user.id.equals(user1.id));
        db.get().users.data.add(user);
        db.get().updateDB();
        return "User was successfully updated";
    }

    @Override
    public String deleteUser(String userID){
        db.get().users.data.removeIf(user -> userID.equals(user.id));
        db.get().updateDB();
        return "User was successfully deleted";
    }

}
