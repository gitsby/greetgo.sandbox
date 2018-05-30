package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.AuthInfo;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

/**
 * как составлять контроллеры написано
 * <a href="https://github.com/greetgo/greetgo.mvc/blob/master/greetgo.mvc.parent/doc/controller_spec.md">здесь</a>
 */
@Bean
@Mapping("/auth")
public class AuthController implements Controller {

    public BeanGetter<AuthRegister> authRegister;

    @AsIs
    @NoSecurity
    @Mapping("/login")
    public String login(@Par("accountName") String accountName, @Par("password") String password) {
        return authRegister.get().login(accountName, password);
    }

    @ToJson
    @Mapping("/info")
    public AuthInfo info(@ParSession("personId") String personId) {
        return authRegister.get().getAuthInfo(personId);
    }

    @ToJson
    @Mapping("/userInfo")
    public UserInfo userInfo(@ParSession("personId") String personId) {
        return authRegister.get().getUserInfo(personId);
    }

    @ToJson
    @Mapping("/clients")
    public List<Client> getClients(@Par("paginationPage") String paginationPage) {
        return authRegister.get().getClients(paginationPage);
    }

    @ToJson
    @Mapping("/delete")
    public boolean deleteClient(@Par("index") String index,
                                @Par("paginationPage") String paginationPage) {
        System.out.println("Value: " + index);
        authRegister.get().deleteClient(index);
        return false;
    }

    @ToJson
    @Mapping("/search")
    public List<Client> searchClient(@Par("searchName") String searchName) {
        return authRegister.get().searchClient(searchName);
    }

    @ToJson
    @Mapping("/pagination_page_num")
    public int getPaginationNum() {
        return authRegister.get().getPaginationNum();
    }
}
