package kz.greetgo.sandbox.db.worker;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

public interface WorkerInterface {
  void createTmpTables();
  void createCsvFiles() throws IOException;
  void loadCsvFile() throws IOException, SAXException;
  void loadCsvFilesToTmpTables() throws IOException, SQLException;
  void fuseMainTmpTables();
  void validateMainTmpTables();
  void migrateMainTmpTableToTables();
  void fuseChildTmpTables();
  void validateChildTmpTables();
  void migrateChildTmpTablesToTables();
  void deleteTmpTables();
  void finish() throws SQLException, IOException;
}
