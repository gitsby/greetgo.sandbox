package kz.greetgo.sandbox.db.beans.prod;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.PoliceCheckServiceConfig;
import kz.greetgo.sandbox.db.fakes.PoliceCheckServiceFake;
import kz.greetgo.sandbox.db.in_service.PoliceCheckService;
import kz.greetgo.sandbox.db.in_service.impl.PoliceCheckServiceReal;

import java.util.concurrent.atomic.AtomicReference;


@SuppressWarnings("unused")
public class PoliceCheckServiceFactory_hotVariant {

  public BeanGetter<PoliceCheckServiceConfig> config;

  private final AtomicReference<PoliceCheckServiceReal> real = new AtomicReference<>(null);

  @Bean(singleton = false)
  public PoliceCheckService createPoliceCheckService() {
    if (config.get().useFake()) return new PoliceCheckServiceFake();

    {
      PoliceCheckServiceReal service = real.get();
      if (service != null) return service;
    }

    synchronized (real) {
      {
        PoliceCheckServiceReal service = real.get();
        if (service != null) return service;
      }

      {
        String url = config.get().url(), username = config.get().username(), password = config.get().password();

        PoliceCheckServiceReal service = new PoliceCheckServiceReal(url, username, password);
        real.set(service);
        return service;
      }
    }

  }
}
