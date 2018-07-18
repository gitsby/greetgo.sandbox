package kz.greetgo.sandbox.db.stand.model;

import java.sql.Timestamp;

public class ClientTransactionDot {

  public int id;
  public int account;
  public double money;
  public Timestamp finished_at;
  public int type;
}
