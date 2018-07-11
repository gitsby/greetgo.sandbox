package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
//import kz.greetgo.sandbox.db.stand.model.PersonDot;

import com.google.gson.Gson;
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
    public int lastId = 0;
    public Accounts accounts = new Accounts();
    public TableToSend table = new TableToSend();
    public Filter filter = new Filter();

    public  Gson gson  = new Gson();

    // TODO: Cool!
    // TODO: But you should think about your other teammates. Don't use the absolute path, change it to relative.
    // TODO: Cause you're not alone on the project.
    // TODO: But the main reason is the project itself becomes inflexible
    // TODO: Make commit and push these files too.
    // TODO: + change the source package. Directory "beans" is for beans only.
    /* I know that it doesn't look good, but it was the fastest and dumbest way to do it ^_^
    * */
    public String usersPath="D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandDbJsonData.json";
    public String accountsPath = "D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandAccountsDb.json";

    @Override
    public void afterInject() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(usersPath));
        users = gson.fromJson(bufferedReader, ArrayUsers.class);
        bufferedReader = new BufferedReader(new FileReader(accountsPath));
        accounts = gson.fromJson(bufferedReader,Accounts.class);
        tableCreate();
    }

    public void tableCreate(){

        String filterText = "[\\s\\S]*";
        FilterType filterType = FilterType.NAME;

        Boolean wasEmpty = true;
        if(!(filter.filterText==null) && !filter.filterText.equals("")){
            filterText = filter.filterText;
            wasEmpty = false;
        }else{
            wasEmpty = true;
        }

        if(!(filter.filterType==null) ){
            filterType = filter.filterType;
        }

        table.table.clear();

        for(int i=0; i<users.data.size(); i++){
            if(users.data.get(i).id>lastId){
                lastId=users.data.get(i).id;
            }
            if(wasEmpty){
                add(i);
            }else
             switch (filterType){

                case NAME:
                    if(filterText.matches(users.data.get(i).name)){ add(i); }
                    break;
                case SURNAME:
                    if(filterText.matches(users.data.get(i).surname)){ add(i); }
                    break;
                case PATRONYMIC:
                    if(filterText.matches(users.data.get(i).patronymic)){ add(i); }
                    break;
                default:
                    add(i);
                    break;
            }
        }
    }

    private void add(int i){
        TableModel tableModel = new TableModel();
        tableModel.fullName= users.data.get(i).surname + " " + users.data.get(i).name + " " + users.data.get(i).patronymic;
        tableModel.id = users.data.get(i).id;
        tableModel.charm = users.data.get(i).charm;
        tableModel.age = users.data.get(i).birthDate;
        tableModel.minBalance=accounts.data.stream().filter((account) -> tableModel.id==account.userID).min(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
        tableModel.maxBalance=accounts.data.stream().filter((account) -> tableModel.id==account.userID).max(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
        tableModel.totalBalance=accounts.data.stream().filter((account) -> tableModel.id==account.userID).mapToDouble(Account::getMoneyNumber).reduce((s1,s2)->(s1+s2)).orElse(0);
        table.table.add(tableModel);
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
