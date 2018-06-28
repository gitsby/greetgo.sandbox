package kz.greetgo.sandbox.controller.model;


public class Account {
  public int id;
  public int userID;
  public double moneyNumber;
  public long registeredAt;


  @Override
  public String toString(){
    return Integer.toString(id)+" "+userID+" "+Double.toString(moneyNumber)+" "+registeredAt;
  }


  public double getMoneyNumber(){
    return moneyNumber;
  }
}
