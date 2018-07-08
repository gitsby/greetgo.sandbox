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
    public TableToSend getTableData(@Par("skipNumber") Integer skipNumber, @Par("limit") Integer limit,
                                    @Par("sortDirection") String sortDirection, @Par("sortType") String sortType,
                                    @Par("filterType") String  filterType, @Par("filterText") String filterText) {
        return tableRegister.get().getTableData(skipNumber,limit, sortDirection, sortType, filterType, filterText);
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-charms")
    public String[] getCharms(){
        return tableRegister.get().getCharms;
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-exact-user")
    public User getExactUser(@Par("userID") Integer userID){
        return tableRegister.get().getExactUser(userID);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/create-user")
    public Integer createUser(@Par("user") @Json User user) {
        return tableRegister.get().createUser(user);
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
    public String deleteUser(@Par("userID") Integer userID){
        return tableRegister.get().deleteUser(userID);
    }



}
