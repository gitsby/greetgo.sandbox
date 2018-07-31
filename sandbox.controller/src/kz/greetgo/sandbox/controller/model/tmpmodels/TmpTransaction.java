package kz.greetgo.sandbox.controller.model.tmpmodels;

import java.util.Date;

public class TmpTransaction {
    public String account_number;
    public String type;
    public String finished_at;
    public String money;
    public String registered_at;
    public String transaction_type;
    public String client_id;

    @Override
    public String toString() {
        return "TmpTransaction{" +
                "account_number='" + account_number + '\'' +
                ", type='" + type + '\'' +
                ", finished_at='" + finished_at + '\'' +
                ", money='" + money + '\'' +
                ", registered_at='" + registered_at + '\'' +
                ", transaction_type='" + transaction_type + '\'' +
                ", client_id='" + client_id + '\'' +
                '}';
    }
}
