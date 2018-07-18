package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public class Client {
  public Integer id;
  public String surname;
  public String name;
  public String patronymic;
  public GenderEnum gender;
  public Date birthDate;
  public Integer charmId;
  public String ciaId;

  @Override
  public String toString() {
    return "{" +
      "id=" + id +
      ", surname='" + surname + '\'' +
      ", name='" + name + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", gender=" + gender +
      ", birthDate=" + birthDate +
      ", charmId=" + charmId +
      ", ciaId='" + ciaId + '\'' +
      "}\n";
  }
}