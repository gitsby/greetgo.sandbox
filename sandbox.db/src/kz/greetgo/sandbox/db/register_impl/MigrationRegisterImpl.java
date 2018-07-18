package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.Ssh;
import kz.greetgo.sandbox.db.util.ArchiveUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  private static Logger logger = Logger.getLogger(MigrationRegisterImpl.class.getName());

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<Ssh> ssh;

  @Override
  public void start() {
    logger.info("start migration.");
    ssh.get().connect();
    migrateFiles(ssh.get().loadMigrationFiles());
    ssh.get().close();
    logger.info("finish migration.");
  }

  private void migrateFiles(List<File> files) {
    logger.info("migrate files " + files);
    files.sort(Comparator.comparing(File::getName));
    for (File file : files) {
      try {
        logger.warn("Unzip file "+file.getName());
        migrateFile(ArchiveUtil.unzip(file));
      } catch (Exception e) {
        logger.error("Unzip file "+file.getPath(), e);
      }
    }
  }

  private void migrateFile(File file) {
    try (Migration migration = new Migration(); Connection connection = getConnection()) {
      File errors = migration.migrate(connection, file);
      ssh.get().uploadFile(errors);
    } catch (Exception e) {
      logger.error("Migrate file "+file.getPath(), e);
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password());
  }
}