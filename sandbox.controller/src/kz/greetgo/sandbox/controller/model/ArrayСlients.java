package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;

public class ArrayСlients {
    public ArrayList<Client> data = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Client client : data) {
            sb.append(client.toString()).append("\n");
        }
        return sb.toString();
    }
}
