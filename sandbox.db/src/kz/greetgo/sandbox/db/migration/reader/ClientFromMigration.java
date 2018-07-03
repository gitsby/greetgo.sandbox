package kz.greetgo.sandbox.db.migration.reader;

import java.util.List;

public class ClientFromMigration {

  public String id;

  public String name;

  public String surname;

  public String patronymic;

  public String birth;

  public String gender;

  public String charm;

  public List<String> mobilePhone;

  public List<String> workPhone;

  public List<String> homePhone;

  public List<AddressFromMigration> addresses;

  public String toInsertString(StringBuilder error) {
    return "insert into temp_client(client_id, name, surname, patronymic, gender, charm, error, created_at) " +
      "values('" + id + "','" + name + "','" + surname + "','" + patronymic + "','" + gender + "','" + charm + "','" + error.toString() + "',current_timestamp);";
  }

  @Override
  public String toString() {
    return "ClientFromMigration{" +
      "id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", birth='" + birth + '\'' +
      ", gender='" + gender + '\'' +
      ", mobilePhone=" + mobilePhone +
      ", workPhone=" + workPhone +
      ", homePhone=" + homePhone +
      ", addresses=" + addresses +
      '}';
  }
}