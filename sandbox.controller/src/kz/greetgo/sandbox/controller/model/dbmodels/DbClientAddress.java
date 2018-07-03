package kz.greetgo.sandbox.controller.model.dbmodels;

import kz.greetgo.sandbox.controller.model.AddressType;

public class DbClientAddress {
    public int client;
    public String type;
    public String street;
    public String house;
    public String flat;

    @Override
    public String toString() {
        return "DbClientAddress{" +
                "client=" + client +
                ", type='" + type + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", flat='" + flat + '\'' +
                '}';
    }
}
