package kz.greetgo.sandbox.controller.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

public class EditClient {

    public Integer id;
    public String name;
    public String surname;
    public String patronymic;
    public String gender;
    public Date birthDate;

    public Integer charm;

    public Address[] addedAddresses;
    public Address[] editedAddresses;
    public Address[] deletedAddresses;

    public Phone[] addedPhones;
    public Phone[] deletedPhones;
    public Phone[] editedPhones;

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
