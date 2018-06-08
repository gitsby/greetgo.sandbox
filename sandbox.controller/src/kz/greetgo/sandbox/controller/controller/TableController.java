package kz.greetgo.sandbox.controller.controller;
//package kz.greetgo.sandbox.controller.controller;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.controller.model.User;

import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.POST;

@Bean
@Mapping("/table")
public class TableController implements Controller{
    public BeanGetter<TableRegister> tableRegister;
    private String userID;


    @AsIs
    @NoSecurity
    @ToJson
    @Mapping("/get-table-data")
    public ArrayUsers getTableData(@Par("skip") int skipNumber, @Par("limit") int limit, @Par("sortDirection") char sortDirection, @Par("sortType") char sortType) {
        System.out.println(skipNumber);
        System.out.println(limit);
        System.out.println(sortDirection);
        System.out.println(sortType);
        return StandJsonDb.Users;
//                tableRegister.get().getTableData( skipNumber, limit, sortDirection, sortType);
    }

    @AsIs
    @NoSecurity
    @Mapping("/get-table-size")
    public int getTableSize(){
        return tableRegister.get().tableSize();
    }


    @AsIs
    @NoSecurity
    @ToJson
    @Mapping("/get-exact-user")
    public User getExactUser(@Par("userID") String userID){
        return tableRegister.get().getExactUser(userID);
    }


    @AsIs
    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/create-user")
    public String createUser(@Par("user") User user) {
        return tableRegister.get().createUser(user);
    }



    @AsIs
    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/change-user")
    public String changeUser(@Par("user") User user) {
        return tableRegister.get().changeUser(user);
    }


    @AsIs
    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/delete-user")
    public String deleteUser(@Par("userID") String userID){
        return tableRegister.get().deleteUser(userID);
    }


    @AsIs
    @NoSecurity
    @ToJson
    @Mapping("/check-if-there-user")
    public Boolean checkIfThereUser(@Par("userID") String userID) {

        System.out.println(userID instanceof String);
        return tableRegister.get().checkIfThereUser(userID);
    }

}
