package kz.greetgo.sandbox.db.worker;

import java.io.IOException;
import java.sql.SQLException;

public interface WorkerInterface {
  void prepareStatements() throws SQLException;
  void createTmpTables() throws SQLException;
  void createCsvFiles();
  void loadCsvFile();
  void loadCsvFilesToTmp();
  void fuseTmpTables();
  void validateTmpTables();
  void migrateToTables();
  void deleteTmpTables();
  void finish() throws SQLException, IOException;
}
