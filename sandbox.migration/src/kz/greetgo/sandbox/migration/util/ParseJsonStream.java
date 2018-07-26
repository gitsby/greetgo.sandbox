package kz.greetgo.sandbox.migration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.sandbox.migration.model.TMPTransaction;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ParseJsonStream {
    public static void parse(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(ObjectMapper.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        System.out.println(br.readLine());
        System.out.println(mapper.readValue(br.readLine(),TMPTransaction.class));

    }
    public static void main(String[] args) throws IOException {

        File file = new File("D:\\greetgonstuff\\greetgo.sandbox\\frs.txt");
        FileInputStream fis = new FileInputStream(file);
        parse(fis);

    }

}
