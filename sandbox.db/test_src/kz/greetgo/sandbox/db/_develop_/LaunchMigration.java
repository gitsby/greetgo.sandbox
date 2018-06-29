package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.configs.ConnectionConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.configs.SSHConfig;
import kz.greetgo.sandbox.db.core.Migration;
import kz.greetgo.sandbox.db.core.SSH;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.ConnectionUtils;

import java.io.File;

public class LaunchMigration {

  public static void main(String[] args) throws Exception {

    final File file = new File("build/__migration__");
    file.getParentFile().mkdirs();
    file.createNewFile();

    ConnectionConfig connectionConfig = ConnectionUtils.fileToConnectionConfig(ConfigFiles.db());
    SSHConfig sshConfig = ConnectionUtils.fileToSSHConfig(ConfigFiles.ssh());
    MigrationConfig migrationConfig = new MigrationConfig() {
      @Override
      public int uploadMaxBatchSize() {
        return 50_000;
      }

      @Override
      public int downloadMaxBatchSize() {
        return 100_000;
      }
    };

    String fileName = "from_cia_2018-02-21-154932-4-300000.xml.tar.bz2";

    File migrationFile;

    try (SSH ssh = new SSH(sshConfig)) {
      ssh.connect();
      ssh.createChanel();
      File sshFile = new File(ssh.getPath(fileName, "/Users/tester/migrationFolder"));
      migrationFile = UnzipUtil.unzip(ssh.load(sshFile.getParent(), sshFile.getName()));
    }

    try (Migration migration = new Migration(connectionConfig, migrationFile, migrationConfig)) {
      migration.migrate();
    }

    file.delete();
  }
}
