package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.MockRequestRegister;

@Bean
public class MockRequestRegisterStand implements MockRequestRegister{
    @Override
    public String getMockRequest() {
        return "WORKS";
    }

}
