package kz.greetgo.sandbox.db.migration.reader;

import java.sql.SQLException;
import java.util.List;

public interface PhoneProcessor {

  void sendPhones(List<PhoneFromMigration> phonesFromMigration) throws SQLException;
}
