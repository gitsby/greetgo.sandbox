package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;

public class Table {
    public ArrayList<TableModel> data = new ArrayList<>();

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(TableModel tableModel:data) {
           sb.append(tableModel.toString()).append("\n");
        }
        return sb.toString();
    }
}
