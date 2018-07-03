package kz.greetgo.sandbox.db.worker;

import java.io.IOException;
import java.sql.SQLException;

public interface WorkerInterface {
  void prepareStatements() throws SQLException;
  void createTmpTables() throws SQLException;
  void createCsvFiles();
  void loadCsvFile();
  void loadCsvFilesToTmp();
  void finish() throws SQLException, IOException;
}
