package kz.greetgo.sandbox.controller.model.tmpmodels;

public class TmpPhone {
    public String phoneType;
    public String number;

    @Override
    public String toString() {
        return "TmpPhone{" +
                "phoneType='" + phoneType + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
