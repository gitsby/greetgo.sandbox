package kz.greetgo.sandbox.db.migration.reader.objects;

public class ClientFromMigration {

  public String id;

  public String name;

  public String surname;

  public String patronymic;

  public String birth;

  public String gender;

  public String charm;

  public String getInsertString() {
    return "insert into temp_client(client_id, name, surname, patronymic, gender, charm, error, created_at)" +
      "values(?,?,?,?,?,?,?,current_timestamp);";
  }

  public String toInsertString(StringBuilder error) {
    return "insert into temp_client(client_id, name, surname, patronymic, gender, charm, error, created_at) " +
      "values('" + id + "','" + name + "','" + surname + "','" + patronymic + "','" + gender + "','" + charm + "','" + error.toString() + "',current_timestamp);";
  }

}