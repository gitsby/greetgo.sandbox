package kz.greetgo.sandbox.db.migration.reader;

import java.sql.SQLException;
import java.util.List;

public interface AddressProcessor {

  void sendAddresses(List<AddressFromMigration> address) throws SQLException;
}
