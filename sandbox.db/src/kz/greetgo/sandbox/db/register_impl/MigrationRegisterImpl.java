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
import java.util.Comparator;
import java.util.List;

@Bean
public class MigrationRegisterImpl implements MigrationRegister {

  private static Logger logger = Logger.getLogger(MigrationRegisterImpl.class);

  public BeanGetter<JdbcSandbox> jdbc;
  public BeanGetter<Ssh> ssh;

  @Override
  public void start() {
    ssh.get().connect();
    migrateFiles(ssh.get().loadMigrationFiles());
    ssh.get().close();
  }

  private void migrateFiles(List<File> files) {
    files.sort(Comparator.comparing(File::getName));
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
}