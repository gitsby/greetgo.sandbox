package kz.greetgo.sandbox.controller.model;

public class TableModel {
    public int id;
    public String fullName;
    public String charm;
    public long age;
    public double totalBalance;
    public double minBalance;
    public double maxBalance;

    @Override
    public String toString(){
        return "\n\t\t\t"+id +" "+ fullName +" "+ charm +" "+ age +" "+ totalBalance +" "+ minBalance +" "+ maxBalance+"\n";
    }




}
