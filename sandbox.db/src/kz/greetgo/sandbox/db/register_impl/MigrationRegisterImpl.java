package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.Ssh;
import kz.greetgo.sandbox.db.util.TarUtil;
import org.apache.log4j.Logger;
import org.fest.util.Files;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  private static Logger logger = Logger.getLogger("migration");

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<Ssh> ssh;

  @Override
  public void start() {
    try {
      migrate();
    } catch (InterruptedException | IOException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void migrate() throws InterruptedException, SQLException, IOException {
    logger.info("Start migration.");
    ssh.get().connect();
    migrateFiles(ssh.get().loadMigrationFiles());
    ssh.get().close();
    logger.info("Finish migration.");
  }

  private void migrateFiles(List<File> files) throws IOException, InterruptedException, SQLException {
    logger.info("Migrate files " + files);
    files.sort(Comparator.comparing(File::getName));
    for (File file : files) {
      logger.info("Migrate file " + file.getName());
      migrateFile(TarUtil.untar(file), getConnection());
      ssh.get().renameToMigrated(file.getName());
    }
  }

  private void migrateFile(File file, Connection connection) throws IOException {
    try (Migration migration = new Migration()) {
      File errors = migration.migrate(connection, file);
      ssh.get().uploadFile(errors);
    } finally {
      Files.delete(file);
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password());
  }
}