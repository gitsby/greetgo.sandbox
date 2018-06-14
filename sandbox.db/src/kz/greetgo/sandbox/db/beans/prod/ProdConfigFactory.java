package kz.greetgo.sandbox.db.beans.prod;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.configs.PoliceCheckServiceConfig;
import kz.greetgo.sandbox.db.util.LocalConfigFactory;

@Bean
public class ProdConfigFactory extends LocalConfigFactory {

  @Bean
  public PoliceCheckServiceConfig createPoliceCheckServiceConfig() {
    return createConfig(PoliceCheckServiceConfig.class);
  }

}
