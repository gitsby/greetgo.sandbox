package kz.greetgo.sandbox.controller.model.tmpmodels;

import java.util.ArrayList;

public class TmpClientDetails {

    public String number;
    public String id;
    public String name;
    public String patronymic;
    public String surname;
    public String gender;
    public String birthDate;
    public ArrayList<TmpPhone> tmpPhones;
    public TmpAddress tmpFacAddress;
    public TmpAddress tmpRegAddress;

    @Override
    public String toString() {
        return "TmpClientDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", ciaId='" + number + '\'' +
                ", gender='" + gender + '\'' +
                ", TmpPhones=" + tmpPhones +
                ", tmpFacAddress=" + tmpFacAddress +
                ", tmpRegAddress=" + tmpRegAddress +
                '}';
    }
}
