package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.register.TestRegister;
import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
//import kz.greetgo.sandbox.controller.model.AuthInfo;
//import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/table")
public class TestController {
    public BeanGetter <TestRegister> testRegister;
//    @ToJson
    @AsIs
    @NoSecurity
    public String testData(){
        return testRegister.get().getTestData();
    }
}
