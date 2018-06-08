package kz.greetgo.sandbox.db.stand.beans;
import com.google.gson.Gson;
import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CheckForJsonUsage {
    public static ArrayUsers Users = new ArrayUsers();
    public static Gson gson = new Gson();
    public static void main(String[] args){
        try {
            String filePath = new File("").getAbsolutePath();
            filePath=filePath+"/sandbox.stand/db/src/kz/greetgo/sandbox/db/stand.beans/";
            filePath="D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandDbJsonData.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            Users=gson.fromJson(bufferedReader,ArrayUsers.class);
            System.out.println(Users.toString());
            System.out.println(getExactUser("1").toString());
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public static User getExactUser(String userID){
        return Users.data.stream().filter((user) -> userID.equals(user.id)).findFirst().get();
    }
}