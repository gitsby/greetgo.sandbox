package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.register.MockRequestRegister;
import java.util.Date;
@Bean
public class MockRequestRegisterStand implements MockRequestRegister{
    @Override
    public Date getMockRequest() {
        return new Date();
    }

}
