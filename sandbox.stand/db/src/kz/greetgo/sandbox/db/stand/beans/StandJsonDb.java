package kz.greetgo.sandbox.db.stand.beans;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.model.User;
//import kz.greetgo.sandbox.db.stand.model.PersonDot;


import java.io.*;

//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

@Bean
public class StandJsonDb implements HasAfterInject{

    public ArrayUsers Users = new ArrayUsers();
//    private final String path = "./StandDbJsonData.json";
    public  Gson gson  = new Gson();
    public String path="D:\\greetgonstuff\\greetgo.sandbox\\sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\StandDbJsonData.json";

    @Override
    public void afterInject() throws Exception {
//        byte[] encoded = Files.readAllBytes(Paths.get("./StandDbJsonData.json"));
//        String json = new String(encoded, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        Users = gson.fromJson(bufferedReader, ArrayUsers.class);
        System.out.println(Users.toString());
    }

    public void updateDB() {
        BufferedWriter bw= null;
        FileWriter fw = null;

        try {
            String jsonText= gson.toJson(Users);
            fw = new FileWriter(path);
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
