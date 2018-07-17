package kz.greetgo.sandbox.db.configs;

import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к SSH")
public interface SshConfig {

  @DefaultStrValue("192.168.26.61")
  String host();

  @DefaultStrValue("adilbekmailanov")
  String user();

  @DefaultStrValue("1q2w3e4r5t6y7u8i9o")
  String password();

  @DefaultStrValue("22")
  Integer port();
}