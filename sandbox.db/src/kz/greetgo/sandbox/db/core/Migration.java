package kz.greetgo.sandbox.db.core;

import kz.greetgo.sandbox.db.configs.ConnectionConfig;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.util.ConnectionUtils;
import kz.greetgo.sandbox.db.util.Informative;
import kz.greetgo.sandbox.db.worker.Worker;
import liquibase.util.file.FilenameUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Migration extends Informative implements Closeable {

  private final ConnectionConfig config;
  private List<Connection> connections;
  private File file;
  private InputStream inputStream;
  private MigrationConfig migrationConfig;

  public Migration(ConnectionConfig config, File file, MigrationConfig migrationConfig) {
    this.config = config;
    this.file = file;
    this.migrationConfig = migrationConfig;
    connections = new ArrayList<>();
  }

  @Override
  public void close() {
    closeConnection();
    closeStream();
  }

  private void closeStream() {
    try {
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.inputStream = null;
  }


  private void closeConnection() {
    try {
      for (int i = 0; i < connections.size(); i++) connections.get(i).close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private String tmpClientTable;

  public int migrate() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    tmpClientTable = "migration_" + sdf.format(nowDate);
    info("TMP_TABLE = " + tmpClientTable);

    return download();
  }

  private void createConnection(int count) throws Exception {
    for (int i = 0; i < count; i++)
      connections.add(ConnectionUtils.create(config));
  }

  private void createInputStream() {
    try {
      inputStream = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private int download() throws Exception {

    createConnection(3);
    createInputStream();

    int res = 0;

    for (int i = 0; i < connections.size(); i++)connections.get(i).setAutoCommit(false);
    switch (getFileFormat(file.getPath())) {
      case "xml":
        Worker.getCiaWorker(connections, inputStream, migrationConfig).execute();
        break;
      case "json_row":
        Worker.getFrsWorker(connections, inputStream, migrationConfig).execute();
    }
    for (int i = 0; i < connections.size(); i++)connections.get(i).setAutoCommit(true);


    return res;
  }

  private static String getFileFormat(String path) {
    String res = FilenameUtils.getExtension(path);
    if (res.isEmpty()) return null;
    if (res.equals("xml") || res.equals("json_row")) return res;
    return getFileFormat(FilenameUtils.removeExtension(path));
  }
}
