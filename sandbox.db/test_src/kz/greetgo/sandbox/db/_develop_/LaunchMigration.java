package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;
import kz.greetgo.sandbox.db.configs.ConnectionConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.configs.SSHConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.SSH;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintStream;

public class LaunchMigration {

  private static Logger logger = Logger.getLogger("migration_logger");

  public BeanGetter<SSHConfig> sshConfig;
  public BeanGetter<AllConfigFactory> allPostgresConfigFactory;

  public static void main(String[] args) throws Exception {
    LaunchMigration launchMigration = new LaunchMigration();
    launchMigration.launch();
  }

  public void launch() throws Exception {
    ConnectionConfig connectionConfig = ConnectionUtils.fileToConnectionConfig(ConfigFiles.db());
    SSHConfig sshConfig = ConnectionUtils.fileToSSHConfig(ConfigFiles.ssh());

    MigrationConfig migrationConfig = new MigrationConfig() {
      @Override
      public int uploadMaxBatchSize() {
        return 50_000;
      }

      @Override
      public int downloadMaxBatchSize() {
        return 70_000;
      }
    };

    String fileNames[] = {"from_cia_2018-02-21-154929-3-30000.xml.tar.bz2",
      "from_cia_2018-02-21-154932-4-300000.xml.tar.bz2",
      "from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2",
      "from_cia_2018-02-21-154929-2-3000.xml.tar.bz2",
      "from_cia_2018-02-21-154929-1-300.xml.tar.bz2"};
    File migrationFile;
    for (String fileName : fileNames) {
      try (SSH ssh = new SSH(sshConfig)) {
        ssh.connect();
        ssh.createChanel();
        File sshFile = new File(ssh.getPath(fileName, "/Users/tester/migrationFolder"));
        migrationFile = UnzipUtil.unzip(ssh.load(sshFile.getParent(), sshFile.getName()));
      }

      try (Migration migration = new Migration(connectionConfig, migrationFile, migrationConfig)) {
        migration.migrate();
      }
    }
  }

  private void prepareSshConfig() throws Exception {
    File file = allPostgresConfigFactory.get().storageFileFor(SSHConfig.class);
    if (file.exists()) {
      file.getParentFile().mkdirs();
      writeSshConfigFile();
    }
  }

  private void writeSshConfigFile() throws Exception {
    File file = allPostgresConfigFactory.get().storageFileFor(SSHConfig.class);
    try (PrintStream out = new PrintStream(file, "UTF-8")) {
      out.println("host=192.168.26.61");
      out.println("user=Tester");
      out.println("password=123");
      out.println("port=22");
    }
  }
}
