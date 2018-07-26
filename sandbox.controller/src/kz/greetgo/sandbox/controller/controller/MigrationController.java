package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.errors.NoCharmError;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRecordsRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;


@Bean
@Mapping("/migration")
public class MigrationController implements Controller{

    public BeanGetter<MigrationController> migrationController;
    @Mapping("/start")
    public void start(){
        migrationController.get().start();
    }

}
