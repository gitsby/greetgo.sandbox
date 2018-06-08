package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;

public class ArrayUsers {
    public ArrayList<User> data = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (User user: data) {
            sb.append(user.toString()).append("\n");
        }
        return sb.toString();
    }
}
