package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;

public class ClientRecordsToSend {
    public ArrayList<ClientRecord> table = new ArrayList<>();
    public int size = 0;
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(ClientRecord clientRecord :table) {
            sb.append(clientRecord.toString()).append("\n");
        }
        return sb.toString();
    }
}
