package kz.greetgo.sandbox.db.stand.beans;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
//import kz.greetgo.sandbox.db.stand.model.PersonDot;


import java.io.*;

//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.Comparator;
import java.util.List;

@Bean
public class StandJsonDb implements HasAfterInject{

    public ArrayUsers users = new ArrayUsers();
    public Accounts accounts = new Accounts();
    public Table table = new Table();
    
    //    private final String path = "./StandDbJsonData.json";
    public  Gson gson  = new Gson();
    public String usersPath="D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandDbJsonData.json";
    public String accountsPath = "D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandAccountsDb.json";

    @Override
    public void afterInject() throws Exception {
//        byte[] encoded = Files.readAllBytes(Paths.get("./StandDbJsonData.json"));
//        String json = new String(encoded, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(usersPath));
        users = gson.fromJson(bufferedReader, ArrayUsers.class);
        System.out.println(users.toString());

        bufferedReader = new BufferedReader(new FileReader(accountsPath));
        accounts = gson.fromJson(bufferedReader,Accounts.class);
        tableCreate();
    }

    public void tableCreate(){
        for(int i=0; i<users.data.size(); i++){
            TableModel tableModel = new TableModel();
            tableModel.fullName= users.data.get(i).surname + " " + users.data.get(i).name + " " + users.data.get(i).patronymic;
            tableModel.id = users.data.get(i).id;
            tableModel.charm = users.data.get(i).charm;
            tableModel.age = users.data.get(i).birthDate;
            tableModel.minBalance=accounts.data.stream().filter((account) -> tableModel.id.equals(account.userID)).min(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
            tableModel.maxBalance=accounts.data.stream().filter((account) -> tableModel.id.equals(account.userID)).max(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
            tableModel.totalBalance=accounts.data.stream().filter((account) -> tableModel.id.equals(account.userID)).mapToDouble(Account::getMoneyNumber).reduce((s1,s2)->(s1+s2)).orElse(0);
            table.data.add(tableModel);
        }
        System.out.println(table.toString());
    }

    public void updateDB() {
        BufferedWriter bw= null;
        FileWriter fw = null;

        try {
            String jsonText= gson.toJson(users);
            fw = new FileWriter(usersPath);
            bw = new BufferedWriter(fw);
            bw.write(jsonText);
            tableCreate();
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
        bw=null;
        fw=null;
        try {
            String jsonText = gson.toJson(accounts);
            fw = new FileWriter(accountsPath);
            bw = new BufferedWriter(fw);
            bw.write(jsonText);

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
    }
}
}
