package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к SSH")
public interface SSHConfig {

  @DefaultStrValue("127.0.0.1")
  String host();

  @DefaultStrValue("user")
  String user();

  @DefaultStrValue("Secret")
  String password();

  @DefaultStrValue("2020")
  Integer port();
}
