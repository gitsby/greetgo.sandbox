package kz.greetgo.sandbox.db.migration.reader.processors;

import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;

import java.sql.SQLException;
import java.util.List;

public interface ClientProcessor {

  void sendClient(List<ClientFromMigration> client) throws SQLException;
}
