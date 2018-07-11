package kz.greetgo.sandbox.db.migration.connection;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Connector {

  private final String ip;
  private final int port;
  private final String userName;
  private String password;
  private final int timeOut;
  private JSch jSch;

  private Session session;

  private Channel channel;

  public Connector(String ip, int port, String userName, String password, int timeOut) throws Exception {
    this.ip = ip;
    this.port = port;
    this.userName = userName;
    this.password = password;
    this.timeOut = timeOut;
    System.out.println("PINGING IP");
    if (!pingIp(ip)) {
      throw new Exception("IP is not reachable");
    }
    System.out.println("IP is reachable");
  }

  public boolean openConnection() throws JSchException {
    boolean res = false;

    jSch = new JSch();

    session = jSch.getSession(userName, ip, port);

    session.setPassword(password);
    session.setConfig("StrictHostKeyChecking", "no");
    session.setTimeout(timeOut);
    session.connect();
//
//    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
//
//    sftp.connect();

    //sftp.get("/Users/tester/migrationFolder/from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2", "C:\\Programs");

    return res;
  }

  public void sendCommand(String command) throws IOException, JSchException {
    channel = session.openChannel("exec");
    ((ChannelExec) channel).setCommand(command);
    channel.connect();
  }

  public List<String> recData() throws IOException, JSchException {
    List<String> content = new ArrayList<>();
    InputStream stream = channel.getInputStream();

    int readByte = stream.read();

    while (readByte != 0xffffffff) {
      content.add(((char) readByte)+"");
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

  public void downloadFile(String filePath) throws JSchException, SftpException {
    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
    sftp.connect();
    sftp.get(filePath, "C:\\Programs");
  }

  public static void main(String[] args) throws Exception {
    Connector connector = new Connector("192.168.26.61", 22, "Tester", "123", 120000);

    connector.openConnection();
    connector.sendCommand("cd migrationFolder; ls");

    System.out.println(connector.recData());
    System.out.println("CONNECTED");
    connector.close();
  }
}
