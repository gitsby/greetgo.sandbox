package kz.greetgo.sandbox.db.migration_util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.controller.model.tmpmodels.TmpTransaction;

import java.io.*;

public class ParseJsonStream {
    public TmpTransaction parse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(ObjectMapper.class);
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        System.out.println(br.readLine());
        return mapper.readValue(json,TmpTransaction.class);
    }
    public static void main(String[] args) throws IOException {
//        File file = new File("D:\\greetgonstuff\\greetgo.sandbox\\frs.txt");
//        FileInputStream fis = new FileInputStream(file);
//        parse(fis);

    }

}
