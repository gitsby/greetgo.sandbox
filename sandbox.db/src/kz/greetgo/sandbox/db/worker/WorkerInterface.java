package kz.greetgo.sandbox.db.worker;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface WorkerInterface {
  void fillTmpTables();
  void margeTmpTables();
  void validTmpTables();
  void migrateTmpTables();
  void deleteTmpTables();
  File getErrorInFile();
  void finish() throws SQLException, IOException;
}
