package kz.greetgo.sandbox.controller.register;


import java.io.Closeable;

/**
 * Работа с миграцией
 */
public interface MigrationRegister extends Closeable {

  void connect();

  void createTmpTable();

  void validTmpTable();

  void migrateToTables();

  void start();
}
