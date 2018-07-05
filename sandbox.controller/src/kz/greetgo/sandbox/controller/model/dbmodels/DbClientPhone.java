package kz.greetgo.sandbox.controller.model.dbmodels;

import kz.greetgo.sandbox.controller.model.PhoneType;

public class DbClientPhone {
    public int client;
    public String number;
    public String type;
    public Boolean validity;

    @Override
    public String toString() {
        return "DbClientPhone{" +
                "client=" + client +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", validity=" + validity +
                '}';
    }
}
