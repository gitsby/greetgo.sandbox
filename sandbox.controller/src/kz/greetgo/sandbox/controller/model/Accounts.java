package kz.greetgo.sandbox.controller.model;


import java.util.ArrayList;

public class Accounts {
    public ArrayList<Account> data = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Account account:data)
            sb.append(account.toString()).append("\n");
        return sb.toString();
    }

}
