package kz.greetgo.sandbox.controller.model;

import java.util.Arrays;

public class User {
    public Integer id;
    public String surname;
    public String name;
    public String patronymic;
    public Long birthDate;
    public String charm;
    public Phone[] phones;
    public Address factualAddress;
    public Address registeredAddress;
    public Boolean validity;

    public GenderType genderType;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthDate=" + birthDate +
                ", charm='" + charm + '\'' +
                ", phones=" + Arrays.toString(phones) +
                ", factualAddress=" + factualAddress +
                ", registeredAddress=" + registeredAddress +
                ", validity=" + validity +
                ", genderType=" + genderType +
                '}';
    }
}


