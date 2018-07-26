package kz.greetgo.sandbox.controller.model;


public class Account {
  public int id;
  public int clientId;
  public double moneyNumber;
  public long registeredAt;


  @Override
  public String toString(){
    return Integer.toString(id)+" "+clientId+" "+Double.toString(moneyNumber)+" "+registeredAt;
  }


  public double getMoneyNumber(){
    return moneyNumber;
  }
}
