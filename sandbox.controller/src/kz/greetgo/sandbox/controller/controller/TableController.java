package kz.greetgo.sandbox.controller.controller;
//package kz.greetgo.sandbox.controller.controller;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.model.Table;
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

    @NoSecurity
    @ToJson
    @Mapping("/get-table-data")
    public Table getTableData(@Par("skip") int skipNumber, @Par("limit") int limit, @Par("sortDirection") String sortDirection, @Par("sortType") String sortType) {
        System.out.println(skipNumber);
        System.out.println(limit);
        System.out.println(sortDirection);
        System.out.println(sortType);
//        StandJsonDb.Users;
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
        System.out.println(user.toString());
     try{
        return tableRegister.get().createUser(user);
    }catch (Exception e){
         e.printStackTrace();
     }
     finally {
         return "fuck you and your user";
     }
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

        System.out.println(userID instanceof String);
        return tableRegister.get().checkIfThereUser(userID);
    }

}
