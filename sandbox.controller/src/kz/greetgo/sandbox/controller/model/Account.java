package kz.greetgo.sandbox.controller.model;


public class Account {
  public int id;
  public String userID;
  public int moneyNumber;
  public long registeredAt;

  @Override
  public String toString(){:
    return Integer.toString(id)+" "+userID+" "+Integer.toString(moneyNumber)+" "+Date
  }
}
