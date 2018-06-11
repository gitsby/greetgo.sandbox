package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;
import kz.greetgo.sandbox.db.stand.beans.StandJsonDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class TableRegisterStand implements TableRegister {

    public BeanGetter<StandJsonDb> db;

    public enum SortType{
        SUP,
        CHARM,
        AGE,
        TOTALBALANCE,
        MAXBALANCE,
        MINBALANCE,;

    }

    @Override
    public Table getTableData(int skipNumber, int limit, String sortDirection, String sortType){
        Table queriedTable  = new Table();
        queriedTable.data=db.get().table.data.stream().sorted(((o1, o2) -> {
            System.out.println("sortType: " + sortType);
            SortType enumSortType = SortType.valueOf(sortType.toUpperCase());
            switch (enumSortType) {
                case SUP:
                    return "descending".equals(sortDirection)?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
                case CHARM:
                    return "descending".equals(sortDirection)?-o1.charm.compareTo(o2.charm):o1.charm.compareTo(o2.charm);
                case AGE:
                    return "descending".equals(sortDirection)?-Long.compare(o1.age,o2.age):Long.compare(o1.age,o2.age);
                case TOTALBALANCE:
                    return "descending".equals(sortDirection)?-Double.compare(o1.totalBalance,o2.totalBalance):Double.compare(o1.totalBalance,o2.totalBalance);
                case MAXBALANCE:
                    return "descending".equals(sortDirection)?-Double.compare(o1.maxBalance,o2.maxBalance):Double.compare(o1.maxBalance,o2.maxBalance);
                case MINBALANCE:
                    return "descending".equals(sortDirection)?-Double.compare(o1.minBalance,o2.minBalance):Double.compare(o1.minBalance,o2.minBalance);
                default:
                    return "descending".equals(sortDirection)?-o1.id.compareTo(o2.id):o1.id.compareTo(o2.id);
            }
        })).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        return queriedTable;
//        return db.get().table;
    }

    @Override
    public int tableSize(){
//        return 1;
        try {
            return db.get().users.data.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 9999;
        }
    }

    @Override
    public User getExactUser(String userID){

        return db.get().users.data.stream().filter((user) -> userID.equals(user.id)).findFirst().get();
    }

    @Override
    public Boolean checkIfThereUser(String userID){
        return db.get().users.data.stream().anyMatch((user) -> userID.equals(user.id));
    }
//    public void print( string){System.out.print(string);}

    @Override
    public String createUser(User user){
        System.out.println("bitch");
        user.id = Integer.toString(db.get().users.data.size());
        db.get().users.data.add(user);
        Account account = new Account();
        account.registeredAt = System.currentTimeMillis();
        account.id = db.get().accounts.data.size();
        account.userID = user.id;
        account.moneyNumber=0;
        db.get().accounts.data.add(account);
        db.get().updateDB();
        System.out.println(db.get().users.data.contains(user));
        return "User was successfully added";
    }


    private Boolean checkForValidity(User user){
        return true;
    }

    @Override
    public String changeUser(User user){
        if (!checkForValidity(user)){
            return "User you given is not valid";
        }
        db.get().users.data.removeIf(user1 -> user.id.equals(user1.id));
        db.get().users.data.add(user);
        db.get().updateDB();
        System.out.println(db.get().users.data.contains(user));
        return "User was successfully edited";
    }

    @Override
    public String deleteUser(String userID){
        db.get().users.data.removeIf(user -> userID.equals(user.id));
        db.get().updateDB();
        System.out.println(checkIfThereUser(userID));
        return "User was successfully deleted";
    }

}
