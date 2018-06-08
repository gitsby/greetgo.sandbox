package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultBoolValue;
import kz.greetgo.conf.hot.DefaultStrValue;

public interface PoliceCheckServiceConfig {

  @DefaultBoolValue(true)
  boolean useFake();

  @DefaultStrValue("https://api.police.kz/checkers")
  String url();

  @DefaultStrValue("greetgo")
  String username();

  @DefaultStrValue("Secret")
  String password();
}
