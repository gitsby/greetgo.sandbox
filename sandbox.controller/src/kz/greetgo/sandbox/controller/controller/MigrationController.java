package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.util.Controller;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {

  public BeanGetter<MigrationRegister> migrationRegister;

  @MethodFilter(RequestMethod.GET)
  @Mapping("/start")
  public void start() {
    migrationRegister.get().start();
  }
}