package kz.greetgo.sandbox.controller.model.tmpmodels;

public class TmpAddress {
    public String street;
    public String flat;
    public String house;

    @Override
    public String toString() {
        return "TmpAddress{" +
                "street='" + street + '\'' +
                ", flat='" + flat + '\'' +
                ", house='" + house + '\'' +
                '}';
    }
}
