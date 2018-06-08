package kz.greetgo.sandbox.controller.controller;
//package kz.greetgo.sandbox.controller.controller;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.controller.model.User;

import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.POST;

@Bean
@Mapping("/table")
public class TableController implements Controller{
    private BeanGetter<TableRegister> tableRegister;
    private String userID;

    @ToJson
    @Mapping("/get-table")
    public List<User> tableRegister(@Par("skip") int skipNumber, @Par("limit") int limit, @Par("sortDirection") char sortDirection, @Par("sortType") char sortType) {
        return tableRegister.get().getTableData( skipNumber, limit, sortDirection, sortType);
    }

    @ToJson
    @Mapping("/get-table-size")
    public int tableRegister(){
        return tableRegister.get().tableSize();
    }

    @ToJson
    @Mapping("/get-user")
    public User tableRegister(@Par("userID") String userID, @Par("mock") boolean mock){
        return tableRegister.get().getExactUser(userID);
    }

    @ToJson
    @MethodFilter(POST)
    @Mapping("/create-user")
    public String tableRegister(@Par("user") User user, @Par("mock") boolean mock) {
        return tableRegister.get().createUser(user);
    }


    @ToJson
    @MethodFilter(POST)
    @Mapping("/change-user")
    public String tableRegister(@Par("user") User user) {
        return tableRegister.get().changeUser(user);
    }

    @ToJson
    @MethodFilter(POST)
    @Mapping("/delete-user")
    public String tableRegister(@Par("userID") String userID, @Par("mock") int mock){
        return tableRegister.get().deleteUser(userID);
    }

    @ToJson
    @Mapping("/check-user")
    public Boolean tableRegister(@Par("userID") String userID, @Par("mock") char mock) {
        return tableRegister.get().checkIfThereUser(userID);
    }

}
