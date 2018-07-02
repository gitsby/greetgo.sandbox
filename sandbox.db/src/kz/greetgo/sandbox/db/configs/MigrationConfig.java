package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultStrValue;

public interface MigrationConfig {

  @DefaultStrValue("50_000")
  int uploadMaxBatchSize();

  @DefaultStrValue("50_000")
  int downloadMaxBatchSize();
}
