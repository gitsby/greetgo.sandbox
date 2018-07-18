package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultIntValue;
import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("SSH configuration parameters")
public interface SSHConfig {

  @DefaultStrValue("192.168.26.61")
  String ip();

  @DefaultIntValue(22)
  int port();

  @DefaultStrValue("Tester")
  String user();

  @DefaultStrValue("123")
  String password();

  @DefaultIntValue(120000)
  int timeOut();
}
