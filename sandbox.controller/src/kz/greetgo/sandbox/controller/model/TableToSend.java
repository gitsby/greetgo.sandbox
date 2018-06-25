package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;

public class TableToSend {
    public ArrayList<TableModel> table = new ArrayList<>();
    public int size = 0;
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(TableModel tableModel:table) {
            sb.append(tableModel.toString()).append("\n");
        }
        return sb.toString();
    }
}
