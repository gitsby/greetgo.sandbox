package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.MigrationRegister;

@Bean
@Mapping("/migration")
public class MigrationController {


  public BeanGetter<MigrationRegister> migrationRegister;

  @Mapping("/migrate")
  public void migrate() throws Exception {
    migrationRegister.get().migrate();

  }
}
