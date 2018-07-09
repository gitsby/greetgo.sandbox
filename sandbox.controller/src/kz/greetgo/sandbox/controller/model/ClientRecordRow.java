package kz.greetgo.sandbox.controller.model;

public class ClientRecordRow {

  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String charm;

  public int age = 0;
  public double accBalance = 0;
  public double maxBalance = 0;
  public double minBalance = 0;

  public String[] toStringArray() {
    return new String[]{id + "", surname, name, (patronymic == null) ? "" : patronymic, charm, age + "", accBalance + "", minBalance + "", maxBalance + ""};
  }

}
