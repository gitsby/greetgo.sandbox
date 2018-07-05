package kz.greetgo.sandbox.controller.model.dbmodels;

import java.util.Date;

public class DbClientAccount {
    public int id;
    public int client;
    public float money;
    public String number;
    public Date registered_at;
    public Boolean validity;

    @Override
    public String toString() {
        return "DbClientAccount{" +
                "id=" + id +
                ", client=" + client +
                ", money=" + money +
                ", number='" + number + '\'' +
                ", registered_at=" + registered_at +
                ", validity=" + validity +
                '}';
    }
}
