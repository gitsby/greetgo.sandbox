package kz.greetgo.sandbox.db.beans.prod;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.PoliceCheckServiceConfig;
import kz.greetgo.sandbox.db.fakes.PoliceCheckServiceFake;
import kz.greetgo.sandbox.db.in_service.PoliceCheckService;
import kz.greetgo.sandbox.db.in_service.impl.PoliceCheckServiceReal;

@Bean
public class PoliceCheckServiceFactory {

  public BeanGetter<PoliceCheckServiceConfig> config;

  @Bean(singleton = false)
  public PoliceCheckService createPoliceCheckService() {
    if (config.get().useFake()) return new PoliceCheckServiceFake();

    String url = config.get().url(), username = config.get().username(), password = config.get().password();

    return new PoliceCheckServiceReal(url, username, password);
  }
}
