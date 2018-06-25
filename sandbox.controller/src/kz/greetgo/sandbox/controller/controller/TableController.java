package kz.greetgo.sandbox.controller.controller;
//package kz.greetgo.sandbox.controller.controller;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;

@Bean
@Mapping("/table")
public class TableController implements Controller{
    public BeanGetter<TableRegister> tableRegister;
    private String userID;

    @NoSecurity
    @ToJson
    @Mapping("/get-table-data")
    public TableToSend getTableData(@Par("skipNumber") int skipNumber, @Par("limit") int limit, @Par("sortDirection") String sortDirection, @Par("sortType") String sortType) {
        return tableRegister.get().getTableData(skipNumber, limit, sortDirection, sortType);
    }

    @NoSecurity
    @ToJson
    @Mapping("/get-table-size")
    public int getTableSize(){
        return tableRegister.get().tableSize();
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-exact-user")
    public User getExactUser(@Par("userID") String userID){
        return tableRegister.get().getExactUser(userID);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/create-user")
    public String createUser(@Par("user") @Json User user) {
        return tableRegister.get().createUser(user);
    }

    @NoSecurity
    @ToJson
    @Mapping("/get-last-id")
    public String getLastId(){
        return tableRegister.get().getLastId();
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/change-user")
    public String changeUser(@Par("user") @Json User user) {
        return tableRegister.get().changeUser(user);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/delete-user")
    public String deleteUser(@Par("userID") String userID){
        return tableRegister.get().deleteUser(userID);
    }


    @NoSecurity
    @ToJson
    @Mapping("/check-if-there-user")
    public Boolean checkIfThereUser(@Par("userID") String userID) {
        return tableRegister.get().checkIfThereUser(userID);
    }

}
