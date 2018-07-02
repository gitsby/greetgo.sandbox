package kz.greetgo.sandbox.db.migration.reader;

import java.sql.SQLException;
import java.util.List;

public interface ClientProcessor {

  void sendClient(List<ClientFromMigration> client) throws SQLException;
}
