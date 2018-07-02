package kz.greetgo.sandbox.controller.model;

public class TMPClient {
  public String id;
  public String surname;
  public String name;
  public String patronymic;
  public String gender;
  public String birthDate;
  public String charm;

  @Override
  public String toString() {
    return "TMPClient{" +
      "id='" + id + '\'' +
      ", surname='" + surname + '\'' +
      ", name='" + name + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", gender='" + gender + '\'' +
      ", birthDate='" + birthDate + '\'' +
      ", charm='" + charm + '\'' +
      '}';
  }
}
