package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class ClientAccountTransaction {
  public Integer id;
  public Integer accountId;
  public Float money;
  public Date finishedAt;
  public Integer typeId;
//FIXME
  @Override
  public String toString() {
    return "ClientAccountTransaction{" +
      "id=" + id +
      ", accountId=" + accountId +
      ", money=" + money +
      ", finishedAt=" + finishedAt +
      ", typeId=" + typeId +
      "}\n";
  }
}
