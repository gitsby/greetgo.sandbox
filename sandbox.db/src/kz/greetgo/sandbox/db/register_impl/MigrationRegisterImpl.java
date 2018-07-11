package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.Ssh;
import kz.greetgo.sandbox.db.util.ArchiveUtil;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  public BeanGetter<JdbcSandbox> jdbc;
  public BeanGetter<Ssh> ssh;
  public BeanGetter<DbConfig> dbConfig;

  @Override
  public void start() {
    ssh.get().connect();
    migrateFiles(ssh.get().loadMigrationFiles());
    ssh.get().close();
  }

  private void migrateFiles(List<File> files) {
    for (File file : files) {
      try {
        migrateFile(ArchiveUtil.unzip(file));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void migrateFile(File file) {
    try(Connection connection = DriverManager.getConnection(dbConfig.get().url(), dbConfig.get().username(), dbConfig.get().password())) {
      try (Migration migration = new Migration(connection, file)) {
        migration.migrate();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}