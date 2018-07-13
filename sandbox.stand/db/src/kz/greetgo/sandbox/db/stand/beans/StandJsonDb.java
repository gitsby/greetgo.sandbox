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
//import java.util.HashMap;
import java.util.Comparator;

@Bean
public class StandJsonDb implements HasAfterInject{

    public ArrayСlients clients = new ArrayСlients();
    public int lastId = 0;
    public Accounts accounts = new Accounts();
    public ClientRecordsToSend clientRecordsToSend = new ClientRecordsToSend();


    public  Gson gson  = new Gson();

    // TODO: Cool!
    // TODO: But you should think about your other teammates. Don't use the absolute path, change it to relative.
    // TODO: Cause you're not alone on the project.
    // TODO: But the main reason is the project itself becomes inflexible
    // TODO: Make commit and push these files too.
    // TODO: + change the source package. Directory "beans" is for beans only.
    /* I know that it doesn't look good, but it was the fastest and dumbest way to do it ^_^
    * */
    public String clientsPath="D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandDbJsonData.json";
    public String accountsPath = "D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandAccountsDb.json";

    @Override
    public void afterInject() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(clientsPath));
        clients = gson.fromJson(bufferedReader, ArrayСlients.class);
        bufferedReader = new BufferedReader(new FileReader(accountsPath));
        accounts = gson.fromJson(bufferedReader,Accounts.class);
        Filter filter = new Filter();
        filter.filterType=FilterType.NAME;
        filter.filterText="";
        clientRecordsCreate(filter);
    }

    public void clientRecordsCreate(Filter filter){

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

        clientRecordsToSend.table.clear();

        for(int i=0; i<clients.data.size(); i++){
            System.out.println(clients.data.get(i).id);
            if (clients.data.get(i).name.equals("testboi")){
                System.out.println(clients.data.get(i));
            }
            if(clients.data.get(i).id>lastId){
                lastId=clients.data.get(i).id;

            }
            if(wasEmpty){
                add(i);
            }else
             switch (filterType){

                case NAME:
                    if(filterText.matches(clients.data.get(i).name)){ add(i); }
                    break;
                case SURNAME:
                    if(filterText.matches(clients.data.get(i).surname)){ add(i); }
                    break;
                case PATRONYMIC:
                    if(filterText.matches(clients.data.get(i).patronymic)){ add(i); }
                    break;
                default:
                    add(i);
                    break;
            }
        }
    }

    private void add(int i){
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.fullName= clients.data.get(i).surname + " " + clients.data.get(i).name + " " + clients.data.get(i).patronymic;
        clientRecord.id = clients.data.get(i).id;
        clientRecord.charm = clients.data.get(i).charm;
        clientRecord.age = clients.data.get(i).birthDate;
        clientRecord.minBalance=accounts.data.stream().filter((account) -> clientRecord.id==account.clientId).min(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
        clientRecord.maxBalance=accounts.data.stream().filter((account) -> clientRecord.id==account.clientId).max(Comparator.comparing(Account::getMoneyNumber)).get().moneyNumber;
        clientRecord.totalBalance=accounts.data.stream().filter((account) -> clientRecord.id==account.clientId).mapToDouble(Account::getMoneyNumber).reduce((s1, s2)->(s1+s2)).orElse(0);
        clientRecordsToSend.table.add(clientRecord);
    }

    public void updateDB() {
        BufferedWriter bw= null;
        FileWriter fw = null;

        try {
            String jsonText= gson.toJson(clients);
            fw = new FileWriter(clientsPath);
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
