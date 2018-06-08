package kz.greetgo.sandbox.controller.model;

import java.util.Arrays;
import java.util.Date;

public class ClientToSave {

    public int id;
    public String name;
    public String surname;
    public String patronymic;
    public String gender;
    public Date birthDate;

    public String snmn = "Empty";
    public int age = 0;

    public int charm;

    // Addresses
    // TODO: Change to ArrayList
    public Address[] addresses;

    // Phone numbers
    public Phone[] phones;

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", snmn='" + snmn + '\'' +
                ", age=" + age +
                ", charm=" + charm +
                ", addresses=" + Arrays.toString(addresses) +
                ", phones=" + Arrays.toString(phones) +
                '}';
    }
}
