package kz.greetgo.sandbox.db.migration.reader.processors;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface AccountProcessor {

  void sendAccounts(List<NewAccountFromMigration> accounts) throws SQLException, ParseException;
}
