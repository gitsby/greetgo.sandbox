package kz.greetgo.sandbox.controller.model.dbmodels;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DbClient {
    public int id;
    public String surname;
    public String name;
    public String patronymic;
    public String gender;
    public int charm;
    public Date birthDate;
    public Boolean validity;

    @Override
    public String toString() {
        return "DbClient{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", gender='" + gender + '\'' +
                ", charm=" + charm +
                ", birthDate=" + birthDate +
                ", validity=" + validity +
                '}';
    }
}
