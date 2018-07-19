package kz.greetgo.sandbox.db.worker;

import java.io.Closeable;
import java.io.File;

public interface WorkerInterface extends Closeable {
  void fillTmpTables();
  void margeTmpTables();
  void validTmpTables();
  void migrateTmpTables();
  void deleteTmpTables();
  File writeOutErrorData();
}
