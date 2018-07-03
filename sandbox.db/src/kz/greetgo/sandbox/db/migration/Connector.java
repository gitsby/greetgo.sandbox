package kz.greetgo.sandbox.db.migration;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class Connector {

  String host = "localhost";

  Socket socket;
  PrintWriter writer;
  InputStream reader;

  String message = "GET http://192.168.11.23/var/metodology/100_000/from_cia_2018-02-21-154535-4-100000.xml.tar.bz2";

  public Connector(String ip, int port) throws IOException, JSchException {
    if (!pingIp(ip)) {
      throw new Error("IP is not reachable");
    }

    JSch jSch = new JSch();

    Session session = jSch.getSession("user", "192.168.11.23", port);

    Properties properties = new Properties();
    session.setPassword("");
    properties.setProperty("StrictHostKeyChecking", "no");
    session.setConfig(properties);

    session.connect();
    System.out.println("SSH Connected");
    socket = new Socket(ip, port);
    reader = socket.getInputStream();
    writer = new PrintWriter(socket.getOutputStream(), true);

  }

  private boolean pingIp(String ip) throws IOException {
    InetAddress address = InetAddress.getByName(ip);
    boolean reachable = address.isReachable(10000);
    System.out.println(reachable);
    return reachable;
  }

  public BufferedReader getContent() throws CompressorException {
    writer.println(message);
    BufferedInputStream bis = new BufferedInputStream(reader);
    CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
    return bufferedReader;
  }

  public static void main(String[] args) throws IOException, CompressorException, JSchException {
    Connector connector = new Connector("192.168.11.23", 80);

    BufferedReader reader = connector.getContent();
    String input;
    while ((input = reader.readLine()) != null) {
      System.out.println(input);
    }

  }
}
