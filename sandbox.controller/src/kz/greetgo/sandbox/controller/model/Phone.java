package kz.greetgo.sandbox.controller.model;

public class Phone {
    public String number;
    public PhoneType phoneType;

    @Override
    public String toString(){
        return number+" "+phoneType;
    }

}
