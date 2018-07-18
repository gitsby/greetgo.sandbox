package kz.greetgo.sandbox.db.migration.connection;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SSHConnector {

  private final String ip;
  private final int port;
  private final String userName;
  private String password;
  private final int timeOut;
  private JSch jSch;

  private Session session;

  private Channel channel;

  public SSHConnector(String ip, int port, String userName, String password, int timeOut) throws Exception {
    this.ip = ip;
    this.port = port;
    this.userName = userName;
    this.password = password;
    this.timeOut = timeOut;
    System.out.println("PINGING IP");
    if (!pingIp(ip)) {
      throw new Exception("IP is not reachable");
    }
    System.out.println("IP IS Reachable");
  }

  public boolean openConnection() throws JSchException {
    boolean res = false;

    jSch = new JSch();

    session = jSch.getSession(userName, ip, port);

    session.setPassword(password);
    session.setConfig("StrictHostKeyChecking", "no");
    session.setTimeout(timeOut);
    session.connect();

    return res;
  }

  public void sendCommand(String command) throws JSchException {
    channel = session.openChannel("exec");
    ((ChannelExec) channel).setCommand(command);
    channel.connect();
  }

  public List<String> recData() throws IOException, JSchException {
    List<String> content = new ArrayList<>();
    InputStream stream = channel.getInputStream();
    StringBuilder builder = new StringBuilder();
    int readByte = stream.read();

    while (readByte != 0xffffffff) {

      builder.append((char) readByte);
      if (builder.toString().contains(".bz2")) {
        content.add(builder.toString().replace("\n", ""));
        builder = new StringBuilder();
      }

      readByte = stream.read();
    }
    channel.disconnect();
    return content;
  }

  public void close() {
    if (session != null) {
      session.disconnect();
    }

    if (channel != null) {
      channel.disconnect();
    }

    jSch = null;
  }

  private boolean pingIp(String ip) throws IOException {
    InetAddress address = InetAddress.getByName(ip);
    boolean reachable = address.isReachable(10000);
    return reachable;
  }

  public void downloadFile(String file) throws JSchException, SftpException, IOException {
    System.out.println("Downloading:" + file);
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.get("/Users/adilbekmailanov/test/" + file, "build/" + file);
    sendCommand("mv /Users/adilbekmailanov/test/" + file + " /Users/adilbekmailanov/test/[downloaded]" + file);
    recData();
    sftp.disconnect();
  }

  public static SSHConnector getConnection() throws Exception {
    return new SSHConnector("192.168.26.61", 22, "adilbekmailanov", "1q2w3e4r5t6y7u8i9o", 120000);
  }

  public static void main(String[] args) throws Exception {
    SSHConnector connector = new SSHConnector("192.168.26.61", 22, "Tester", "123", 120000);

    connector.openConnection();
    connector.sendCommand("cd test; ls");

    for (String file : connector.recData()) {
      connector.downloadFile(file);
    }

    System.out.println("Downloaded files.");
    connector.close();
  }

  public void uploadErrorFile() throws JSchException, SftpException {
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.put("build/error.csv", "/Users/adilbekmailanov/err/");
    sftp.disconnect();
  }
}
