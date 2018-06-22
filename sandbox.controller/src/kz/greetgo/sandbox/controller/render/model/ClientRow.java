package kz.greetgo.sandbox.controller.render.model;

public class ClientRow {
  public Integer id;
  public String surname;
  public String name;
  public String patronymic;
  public int age;
  public float middle_balance;
  public float max_balance;
  public float min_balance;

  public ClientRow() {
  }

  public ClientRow(Integer id, String surname, String name, String patronymic, int age, float middle_balance, float max_balance, float min_balance) {
    this.id = id;
    this.surname = surname;
    this.name = name;
    this.patronymic = patronymic;
    this.age = age;
    this.middle_balance = middle_balance;
    this.max_balance = max_balance;
    this.min_balance = min_balance;
  }
}