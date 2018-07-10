package kz.greetgo.sandbox.db.worker;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

public interface WorkerInterface {
  void createTmpTables();
  void createCsvFiles() throws IOException;
  void loadCsvFile() throws IOException, SAXException;
  void loadCsvFilesToTmp() throws IOException, SQLException;
  void fuseTmpTables();
  void validateTmpTables();
  void migrateToTables();
  void deleteTmpTables();
  void finish() throws SQLException, IOException;
}
