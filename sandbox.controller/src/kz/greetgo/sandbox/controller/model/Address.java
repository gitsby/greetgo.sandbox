package kz.greetgo.sandbox.controller.model;

public class Address{
    public String  flat;
    public String house;
    public String street;
    public AddressType addressType;

    public String toString(){
        return flat + " " + house + " "+street+" "+addressType;
    }
}