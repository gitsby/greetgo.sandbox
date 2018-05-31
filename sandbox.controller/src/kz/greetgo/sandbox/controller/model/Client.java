package kz.greetgo.sandbox.controller.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class Client {

    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public String gender;
    public String birthDate;

    public String snmn = "Empty";
    public int age = 0;
    public int accBalance = 10;
    public int maxBalance = 0;
    public int minBalance = 0;

    public Character charm;

    // Addresses
    public Address[] addresses;

    // Phone numbers
    public Phone[] phones;

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        String jsonString = "";
        try {
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
            jsonGenerator.setCodec(new ObjectMapper());
            jsonGenerator.writeObject(this);
            jsonGenerator.close();
            jsonString = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
