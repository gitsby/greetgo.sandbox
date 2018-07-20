package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.SshConfig;
import org.fest.util.Files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Bean
public class Ssh implements Closeable {

  public BeanGetter<SshConfig> sshConfig;

  private Session session;
  private ChannelSftp sftpChannel;

  public void connect() {
    try {
      createSession();
      createChanel();
    } catch (JSchException e) {
      throw new RuntimeException(e);
    }
  }

  private void createSession() throws JSchException {
    session = getSession();
    session.setPassword(sshConfig.get().password());
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();
  }

  private Session getSession() throws JSchException {
    return new JSch().getSession(sshConfig.get().user(), sshConfig.get().host(), sshConfig.get().port());
  }

  private void createChanel() throws JSchException {
    sftpChannel = (ChannelSftp) session.openChannel("sftp");
    sftpChannel.connect();
  }

  private File load(File file) {
    File newFile = getNewTmpFile(file.getName());
    try {
      sftpChannel.get(file.getPath(), newFile.getPath());
    } catch (SftpException e) {
      throw new RuntimeException(e);
    }
    return newFile;
  }

  private File getNewTmpFile(String name) {
    File tmpFile =  new File("build/out_files/"+name);
    if (!tmpFile.exists()) tmpFile.getParentFile().mkdirs();
    return tmpFile;
  }

  private File rename(File file, String newName) {
    String newNamePath = file.getParent() + "/" + newName;
    rename(file.getPath(), newNamePath);
    return new File(newNamePath);
  }

  private void rename(String from, String to) {
    try {
      sftpChannel.rename(from, to);
    } catch (SftpException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    sftpChannel.disconnect();
    session.disconnect();
  }

  private List<File> getNotMigratedFiles() throws SftpException {
    List<File> files = new ArrayList<>();
    Vector migrationFilesVector = getMigrationFolderVector();
    for (Object fileVector : migrationFilesVector) {
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) fileVector;
      if (!isNormalFile(entry)) continue;
      if (!isMigrated(entry.getFilename())) files.add(getFile(entry));
    }
    return files;
  }

  private boolean isNormalFile (ChannelSftp.LsEntry entry) {
    return !entry.getFilename().startsWith(".");
  }

  private Vector getMigrationFolderVector() throws SftpException {
    return sftpChannel.ls(getMigrationFolder());
  }

  private File getFile(ChannelSftp.LsEntry entry) {
    return new File(getMigrationFolder() + entry.getFilename());
  }

  private String getMigrationFolder() {
    return System.getProperty("user.home") + "/files_for_migration/";
  }

  private boolean isMigrated(String fileName) {
    return "migration".equals(fileName.substring(0, 9)) || "migrated".equals(fileName.substring(0, 8));
  }

  public List<File> loadMigrationFiles() {
    List<File> filesForMigration = new ArrayList<>();
    try {
      List<File> notMigratedFiles = getNotMigratedFiles();
      for (File file : notMigratedFiles) {
        filesForMigration.add(load(renameToMigration(file)));
      }
    } catch (SftpException e) {
      throw new RuntimeException(e);
    }
    return filesForMigration;
  }

  private File renameToMigration(File file) {
    return rename(file, "migration_"+file.getName());
  }

  public void uploadFile(File errors) {
    try(InputStream inputStream = new FileInputStream(errors)) {
      sftpChannel.put(inputStream, getMigrationFolder()+errors.getName(), ChannelSftp.OVERWRITE);
    } catch (SftpException | IOException e) {
      throw new RuntimeException(e);
    }
    Files.delete(errors);
  }

  public void renameToMigrated(String fileName) {
    rename(getMigrationFolder()+fileName, getMigrationFolder()+"migrated_"+fileName);
  }
}