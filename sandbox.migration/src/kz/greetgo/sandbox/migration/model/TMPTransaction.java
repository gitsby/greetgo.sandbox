package kz.greetgo.sandbox.migration.model;

public class TMPTransaction {
    public String account_number;
    public String type;
    public String finished_at;
    public String money;

    @Override
    public String toString() {
        return "TmpTransaction{" +
                "account_number='" + account_number + '\'' +
                ", type='" + type + '\'' +
                ", finished_at='" + finished_at + '\'' +
                ", money='" + money + '\'' +
                ", transaction_type='" + transaction_type + '\'' +
                '}';
    }

    public String transaction_type;
}
