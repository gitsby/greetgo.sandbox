package kz.greetgo.sandbox.db.migration.reader.objects;

import java.sql.Timestamp;

public class ClientFromMigration {

  public String id;

  public String name;

  public String surname;

  public String patronymic;

  public String birth;

  public String gender;

  public String charm;

  public StringBuilder error;

  public Timestamp timestamp;

  public String getInsertString() {
    return "insert into temp_client(client_id, name, surname, patronymic, gender, charm, error, birth_date,created_at)" +
      "values(?,?,?,?,?,?,?,?,?) on conflict (client_id)\n" +
      "  do update\n" +
      "    set\n" +
      "      surname    = case when EXCLUDED.surname notnull\n" +
      "        then EXCLUDED.surname end,\n" +
      "      name       = case when EXCLUDED.name notnull\n" +
      "        then EXCLUDED.name end,\n" +
      "      patronymic = EXCLUDED.patronymic,\n" +
      "      birth_date = case when EXCLUDED.birth_date notnull\n" +
      "        then EXCLUDED.birth_date end,\n" +
      "      gender     = case when EXCLUDED.gender notnull\n" +
      "        then EXCLUDED.gender end,\n" +
      "      charm      = case when EXCLUDED.charm notnull\n" +
      "        then EXCLUDED.charm end;";
  }

  public String toInsertString(StringBuilder error) {
    return "insert into temp_client(client_id, name, surname, patronymic, gender, charm, error, created_at) " +
      "values('" + id + "','" + name + "','" + surname + "','" + patronymic + "','" + gender + "','" + charm + "','" + error.toString() + "',current_timestamp);";
  }

}