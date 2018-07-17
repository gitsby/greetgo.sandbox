package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.Ssh;
import kz.greetgo.sandbox.db.util.ArchiveUtil;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  private static Logger logger = Logger.getLogger(MigrationRegisterImpl.class);

  public BeanGetter<JdbcSandbox> jdbc;
  public BeanGetter<Ssh> ssh;

  @Override
  public void connect() {
    ssh.get().connect();
    migrateFiles(ssh.get().loadMigrationFiles());
    ssh.get().close();
  }

  @Override
  public void createTmpTable() {

  }

  @Override
  public void validTmpTable() {

  }

  @Override
  public void migrateToTables() {

  }

  @Override
  public void start() {

  }

  private void migrateFiles(List<File> files) {
    for (File file : files) {
      try {
        migrateFile(ArchiveUtil.unzip(file));
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  private void migrateFile(File file) {
    jdbc.get().execute(connection -> {
        try (Migration migration = new Migration()) {
          migration.migrate(connection, file);
        } catch (Exception e) {
          logger.error(e);
        }
        return null;
      }
    );
  }

  @Override
  public void close() throws IOException {

  }
}