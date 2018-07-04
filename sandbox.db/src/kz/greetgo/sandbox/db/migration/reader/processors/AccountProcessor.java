package kz.greetgo.sandbox.db.migration.reader.processors;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;

import java.util.List;

public interface AccountProcessor {

  void sendAccounts(List<NewAccountFromMigration> accounts);
}
