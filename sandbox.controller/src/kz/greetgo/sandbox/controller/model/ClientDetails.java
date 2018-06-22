package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class ClientDetails {

  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public Date birthDate;

  // FIXME: 6/22/18 Зачем тебе age?
  public int age = 0;

  public int charm;

  public List<Address> addresses;

  public List<Phone> phones;

  @Override
  public String toString() {
    return "Client{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", gender='" + gender + '\'' +
      ", birthDate='" + birthDate + '\'' +
      ", age=" + age +
      ", charm=" + charm +
      ", addresses=" + addresses +
      ", phones=" + phones +
      '}';
  }
}
