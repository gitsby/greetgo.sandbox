package kz.greetgo.sandbox.controller.model;

public class Phone {
    public String number;
    public PhoneType phoneType;
    public Boolean validity;

    @Override
    public String toString(){
        return number+" "+phoneType;
    }

}
