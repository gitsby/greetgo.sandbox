package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.sandbox.controller.register.MigrationRegister;

@Bean
@Mapping("/migration")
public class MigrationController {

  public BeanGetter<MigrationRegister> migrationRegister;

  @MethodFilter(RequestMethod.GET)
  @Mapping("/start")
  public void start() {
    migrationRegister.get().start();
  }

}