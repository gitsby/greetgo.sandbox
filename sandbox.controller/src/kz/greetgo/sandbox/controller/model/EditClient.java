package kz.greetgo.sandbox.controller.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class EditClient {

    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public String gender;
    public String birthDate;

    public Character charm;

    Address[] unchangedAddresses;
    Address[] editedAddresses;
    Address[] deletedAddresses;

    Phone[] unchangedPhones;
    Phone[] deletedPhones;
    Phone[] editedPhones;

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
