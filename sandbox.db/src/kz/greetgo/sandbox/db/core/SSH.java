package kz.greetgo.sandbox.db.core;

import com.jcraft.jsch.*;
import kz.greetgo.sandbox.db.configs.SSHConfig;
import kz.greetgo.sandbox.db.util.ConfigFiles;
import kz.greetgo.sandbox.db.util.Informative;

import java.io.Closeable;
import java.io.File;
import java.util.Vector;

public class SSH extends Informative implements Closeable {

  private SSHConfig sshConfig;
  private Session session;
  private ChannelSftp sftpChannel;

  public SSH(SSHConfig sshConfig) {
    this.sshConfig = sshConfig;
  }

  public void connect() throws JSchException {
    JSch jsch = new JSch();
    session = jsch.getSession(sshConfig.user(), sshConfig.host(), sshConfig.port());
    session.setPassword(sshConfig.password());
    session.setConfig("StrictHostKeyChecking", "no");
    info("StrictHostKeyChecking");
    info("Establishing Connection...");
    session.connect();
    info("Connection established.");
  }

  public void createChanel() throws JSchException {
    if (session == null) {
      info("Crating SFTP Channel failed: SSH not connected.");
      return;
    }
    info("Crating SFTP Channel.");
    sftpChannel = (ChannelSftp) session.openChannel("sftp");
    sftpChannel.connect();
    info("SFTP Channel created.");
  }

  public File load(String path, String name) {
    if (path == null || name == null) throw new NullPointerException();
    info("Load file " + name + " from " + path + ".");
    String newFilePath = ConfigFiles.tmpDir()+"/"+name;
    try {
      sftpChannel.get(path+"/"+name, newFilePath);
    } catch (SftpException e) {
      info("Filed load file.");
      return null;
    }
    info("File loaded");
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

  @Override
  public void close() {
    if (sftpChannel != null) sftpChannel.disconnect();
    if (session != null) session.disconnect();
    info("Closed.");
  }
}
