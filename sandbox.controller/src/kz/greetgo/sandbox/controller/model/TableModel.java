package kz.greetgo.sandbox.controller.model;

public class TableModel {
    public String id;
    public String fullName;
    public CharmType charm;
    public long age;
    public double totalBalance;
    public double minBalance;
    public double maxBalance;

    @Override
    public String toString(){
        return id +" "+ fullName +" "+ charm +" "+ age +" "+ totalBalance +" "+ minBalance +" "+ maxBalance;
    }
}
