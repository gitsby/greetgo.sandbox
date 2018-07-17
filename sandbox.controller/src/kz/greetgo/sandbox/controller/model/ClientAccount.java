package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class ClientAccount {
  public Integer id;
  public Integer client;
  public float money;
  public String number;
  public Date registeredAt;
//FIXME
  @Override
  public String toString() {
    return "ClientAccount{" +
      "id=" + id +
      ", client=" + client +
      ", money=" + money +
      ", number='" + number + '\'' +
      ", registeredAt=" + registeredAt +
      "}\n";
  }
}
