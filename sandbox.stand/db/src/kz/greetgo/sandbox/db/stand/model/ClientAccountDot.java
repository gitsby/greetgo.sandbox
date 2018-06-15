package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientAccount;

import java.util.Date;

public class ClientAccountDot {
  public Integer id;
  // FIXME: 6/15/18 clientId
  public Integer client;
  public float money;
  public String number;
  public Date registeredAt;

  public ClientAccount toClientAccount() {
    ClientAccount clientAccount = new ClientAccount();
    clientAccount.id = this.id;
    clientAccount.client = client;
    clientAccount.money = this.money;
    clientAccount.number = this.number;
    clientAccount.registeredAt = this.registeredAt;
    return clientAccount;
  }
}
