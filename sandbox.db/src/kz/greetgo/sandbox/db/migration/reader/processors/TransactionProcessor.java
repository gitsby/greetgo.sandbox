package kz.greetgo.sandbox.db.migration.reader.processors;

import kz.greetgo.sandbox.db.migration.reader.objects.TransactionFromMigration;

import java.util.List;

public interface TransactionProcessor {

  void sendTransactions(List<TransactionFromMigration> transactions);
}
