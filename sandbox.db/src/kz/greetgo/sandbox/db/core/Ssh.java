package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.configs.SshConfig;
import org.apache.log4j.Logger;
import org.fest.util.Lists;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.Vector;

@Bean
public class Ssh implements Closeable {

  private final static Logger logger = Logger.getLogger(Ssh.class);

  public BeanGetter<SshConfig> sshConfig;
  public BeanGetter<MigrationConfig> migrationConfig;

  private Session session;
  private ChannelSftp sftpChannel;

  public void connect() {
    try {
      createSession();
      createChanel();
    } catch (JSchException e) {
      logger.error(e);
    }
  }

  public void createSession() throws JSchException {
    session = getSession();
    session.setPassword(sshConfig.get().password());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();
  }

  private Session getSession() throws JSchException {
    return new JSch().getSession(sshConfig.get().user(), sshConfig.get().host(), sshConfig.get().port());
  }

  public void createChanel() throws JSchException {
    sftpChannel = (ChannelSftp) session.openChannel("sftp");
    sftpChannel.connect();
  }

  public File load(File file) {
    File newFile = getNewTmpFile(file.getName());
    try {
      sftpChannel.get(file.getPath(), newFile.getPath());
    } catch (SftpException e) {
      logger.error(e);
    }
    return newFile;
  }

  private File getNewTmpFile(String name) {
    return new File(migrationConfig.get().tmpFolder()+"/"+name);
  }

  public void rename(File file, String newName) {
//    String newNamePath = file.getParent() + "/" + newName;
//    try {
//      sftpChannel.rename("./migrationFolder/from_cia_2018-02-21-154929-1-300.xml.tar.bz2", "./migrationFolder/M_from_cia_2018-02-21-154929-1-300.xml.tar.bz2");
//    } catch (SftpException e) {
//      logger.error(e);
//    }
  }

  private void renameToMigrated(File file) {
    rename(file, "migrated_"+file.getName());
  }

  @Override
  public void close() {
    sftpChannel.disconnect();
    session.disconnect();
  }

  private List<File> getNotMigratedFiles() throws SftpException {
    List<File> files = Lists.newArrayList();
    Vector migrationFilesVector = getMigrationFolderVector();
    for (Object fileVector : migrationFilesVector) {
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileVector;
      if (!isNormalFile(entry)) continue;
      if (!isMigrated(entry.getFilename())) files.add(getFile(entry));
    }
    return files;
  }

  private boolean isNormalFile (ChannelSftp.LsEntry entry) {
    if (entry.getFilename().equals(".") || entry.getFilename().equals("..")) return false;
    return true;
  }

  private Vector getMigrationFolderVector() throws SftpException {
    return sftpChannel.ls(migrationConfig.get().migrationFilesFolder());
  }

  private File getFile(ChannelSftp.LsEntry entry) {
    return new File(migrationConfig.get().migrationFilesFolder() + "/" + entry.getFilename());
  }

  private boolean isMigrated(String fileName) {
    return fileName.substring(0, 8).equals("migrated");
  }

  public List<File> loadMigrationFiles() {
    List<File> filesForMigration = Lists.newArrayList();
    try {
      List<File> notMigratedFiles = getNotMigratedFiles();
      for (File file : notMigratedFiles) {
        filesForMigration.add(load(file));
        renameToMigrated(file);
      }
    } catch (SftpException e) {
      logger.error(e);
    }
    return filesForMigration;
  }
}