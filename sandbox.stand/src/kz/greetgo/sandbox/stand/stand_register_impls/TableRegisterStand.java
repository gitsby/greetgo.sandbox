package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.model.User;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.controller.security.SecurityError;
import kz.greetgo.sandbox.db.stand.beans.StandJsonDb;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class TableRegisterStand implements TableRegister {

    public BeanGetter<StandJsonDb> db;

    @Override
    public ArrayUsers getTableData(int skipNumber, int limit, char sortDirection, char sortType){
        ArrayList<User> queriedUsers = new ArrayList<>();
        if(sortType=='f'&&sortDirection=='z')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> o1.surname.compareTo(o2.surname)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if(sortType=='f'&&sortDirection=='a')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> -o1.surname.compareTo(o2.surname)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if(sortType=='i'&&sortDirection=='z')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> o1.name.compareTo(o2.name)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if(sortType=='i'&&sortDirection=='a')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> -o1.name.compareTo(o2.name)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if(sortType=='o'&&sortDirection=='z')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> o1.patronymic.compareTo(o2.patronymic)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        if(sortType=='o'&&sortDirection=='a')
            queriedUsers=db.get().Users.data.stream().sorted((o1,o2) -> -o1.patronymic.compareTo(o2.patronymic)).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        ArrayUsers Users = new ArrayUsers();
        Users.data=queriedUsers;
        return Users;
    }

    @Override
    public int tableSize(){
//        return 1;
        try {
            return db.get().Users.data.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 9999;
        }
    }

    @Override
    public User getExactUser(String userID){

        return db.get().Users.data.stream().filter((user) -> userID.equals(user.id)).findFirst().get();
    }

    @Override
    public Boolean checkIfThereUser(String userID){
        return db.get().Users.data.stream().anyMatch((user) -> userID.equals(user.id));
    }
//    public void print( string){System.out.print(string);}

    @Override
    public String createUser(User user){
        System.out.println("bitch");
        user.id = Integer.toString(db.get().Users.data.size());
        db.get().Users.data.add(user);
        db.get().updateDB();
        System.out.println(db.get().Users.data.contains(user));
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
        db.get().Users.data.removeIf(user1 -> user.id.equals(user1.id));
        db.get().Users.data.add(user);
        db.get().updateDB();
        System.out.println(db.get().Users.data.contains(user));
        return "User was successfully edited";
    }

    @Override
    public String deleteUser(String userID){
        db.get().Users.data.removeIf(user -> userID.equals(user.id));
        db.get().updateDB();
        System.out.println(checkIfThereUser(userID));
        return "User was successfully deleted";
    }

}
