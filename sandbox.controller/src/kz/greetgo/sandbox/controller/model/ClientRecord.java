package kz.greetgo.sandbox.controller.model;

public class ClientRecord {
  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String charm;

  public int age = 0;
  public double accBalance = 0;
  public double maxBalance = 0;
  public double minBalance = 0;

  @Override
  public String toString() {
    return "ClientRecord{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", charm='" + charm + '\'' +
      ", age=" + age +
      ", accBalance=" + accBalance +
      ", maxBalance=" + maxBalance +
      ", minBalance=" + minBalance +
      '}';
  }
}
