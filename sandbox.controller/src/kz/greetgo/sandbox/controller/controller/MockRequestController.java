package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.register.MockRequestRegister;
import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/table")
public class MockRequestController implements Controller    {
    public BeanGetter <MockRequestRegister> mockRequestRegister;
    @ToJson
    @AsIs
    @NoSecurity
    @Mapping("")
    public String mockRequest(){
        return mockRequestRegister.get().getMockRequest();
    }
}
