package kz.greetgo.sandbox.controller.model;

public class ClientRecord {
  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public int age;
  public float middle_balance;
  public float max_balance;
  public float min_balance;

  @Override
  public String toString() {
    return "ClientRecord{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", age=" + age +
      ", middle_balance=" + middle_balance +
      ", max_balance=" + max_balance +
      ", min_balance=" + min_balance +
      '}';
  }
}
