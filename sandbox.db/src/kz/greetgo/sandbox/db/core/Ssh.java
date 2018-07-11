package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.configs.SSHConfig;
import kz.greetgo.sandbox.db.util.Informative;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.Vector;

@Bean
public class SSH extends Informative implements Closeable {

  private final static Logger logger = Logger.getLogger(SSH.class);

  public BeanGetter<SSHConfig> sshConfig;
  public BeanGetter<MigrationConfig> migrationConfig;

  private Session session;
  private ChannelSftp sftpChannel;

  public SSH(){
  }

  public void connect() throws JSchException {
    JSch jsch = new JSch();
    session = jsch.getSession(sshConfig.get().user(), sshConfig.get().host(), sshConfig.get().port());
    session.setPassword(sshConfig.get().password());
    session.setConfig("StrictHostKeyChecking", "no");
    logger.info("StrictHostKeyChecking");
    logger.info("Establishing Connection...");
    session.connect();
    logger.info("Connection established.");
  }

  public void createChanel() throws JSchException {
    if (session == null) {
      logger.info("Crating SFTP Channel failed: SSH not connected.");
      return;
    }
    logger.info("Crating SFTP Channel.");
    sftpChannel = (ChannelSftp) session.openChannel("sftp");
    sftpChannel.connect();
    logger.info("SFTP Channel created.");
  }

  public File load(String path, String name) {
    if (path == null || name == null) throw new NullPointerException();
    logger.info("Load file " + name + " from " + path + ".");
    String newFilePath = migrationConfig.get().tmpFolder()+"/"+name;
    try {
      sftpChannel.get(path+"/"+name, newFilePath);
    } catch (SftpException e) {
      logger.error(e.getMessage());
    }
    logger.info("File loaded");
    return new File(newFilePath);
  }

  public String getPath(String fileName) {
    info("Finding path of file " + fileName + "...");
    String res = find(fileName, "/");
    if (res == null) info("File not found.");
    else info("File found in " + res);
    return res;
  }

  public String getPath(String fileName, String path) {
    info("Finding path of file " + fileName + "...");
    String res = find(fileName, path);
    if (res == null) info("File not found.");
    else info("File found in " + res);
    return res;
  }

  private String find(String fileName, String path) {

    Vector vector;

    try {
      vector = sftpChannel.ls(path);
    } catch (SftpException e) {
      logger.error(e.getMessage());
      return null;
    }

    for (int i = 0; i < vector.size(); i++) {
      ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) vector.get(i);
      if (entry.getFilename().equals(fileName)) return path+"/"+entry.getFilename();
      if (isNormalDir(entry)) {
        String res = find(fileName, path+entry.getFilename()+"/");
        if (res != null) return res;
      }
    }

    return null;
  }

  private boolean isNormalDir (ChannelSftp.LsEntry entry) {
    return entry.getAttrs().isDir() && entry.getFilename().matches("\\w*");
  }

  private void rename(File file, String newName) {
    String newNamePath = file.getParent() + "/" + newName;
    logger.info(String.format("Rename from %s to %s", file.getPath(), newNamePath));
    try {
      sftpChannel.rename(file.getAbsolutePath(), newNamePath);
    } catch (SftpException e) {
      logger.error(e.getMessage());
      return;
    }
    logger.info("File renamed");
  }

  @Override
  public void close() {
    if (sftpChannel != null) sftpChannel.disconnect();
    if (session != null) session.disconnect();
    info("Closed.");
  }

  public List<File> loadMigrationFiles() {
    throw new UnsupportedOperationException();
  }
}
