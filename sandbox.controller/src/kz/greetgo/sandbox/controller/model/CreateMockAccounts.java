package kz.greetgo.sandbox.controller.model;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class CreateMockAccounts {
    public static void main(String[] args){
        String[] ids = {"0","1","2","3","4"};
        int[] mins = {123,231};
        int[] maxes = {789,897};
        Accounts accounts = new Accounts();
        for(int i=0; i<5; i++){
            for(int j=0; j<2; j++) {
                Account account = new Account();
                account.userID=Integer.toString(i);
                account.id=accounts.data.size();
                if(j%2==0)
                    account.moneyNumber=mins[(j+i)%2];
                else
                    account.moneyNumber=maxes[(j+i)%2];
                account.registeredAt=System.currentTimeMillis();
                accounts.data.add(account);
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(accounts);
        BufferedWriter bw= null;
        FileWriter fw = null;
        System.out.println(json);
        try{
            fw =new FileWriter((new File("accounts.json").getAbsolutePath()));
            bw = new BufferedWriter(fw);
            bw.write(json);}
            catch (IOException e){
            e.printStackTrace(); }
            finally {
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
