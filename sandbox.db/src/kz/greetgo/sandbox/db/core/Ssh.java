package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.SshConfig;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Bean
public class Ssh implements Closeable {

  private final static Logger logger = Logger.getLogger("callback");

  public BeanGetter<SshConfig> sshConfig;

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
      logger.error(e);
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
    try {
      sftpChannel.rename(file.getPath(), newNamePath);
    } catch (SftpException e) {
      logger.error(e);
    }
    return new File(newNamePath);
  }

  private File renameToMigrated(File file) {
    return rename(file, "migrated_"+file.getName());
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
    return fileName.substring(0, 8).equals("migrated");
  }

  public List<File> loadMigrationFiles() {
    List<File> filesForMigration = new ArrayList<>();
    try {
      List<File> notMigratedFiles = getNotMigratedFiles();
      for (File file : notMigratedFiles) {
        filesForMigration.add(load(renameToMigrated(file)));
      }
    } catch (SftpException e) {
      logger.error(e);
    }
    return filesForMigration;
  }

  public void uploadFile(File errors) {
    try(InputStream inputStream = new FileInputStream(errors)) {
      sftpChannel.put(inputStream, getMigrationFolder()+errors.getName(), ChannelSftp.OVERWRITE);
    } catch (Exception e) {
      logger.error(e);
    }
    errors.delete();
  }
}