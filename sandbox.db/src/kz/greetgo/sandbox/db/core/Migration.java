package kz.greetgo.sandbox.db.core;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.worker.Worker;
import liquibase.util.file.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.util.Objects;

@Bean
public class Migration implements Closeable {

  private static Logger logger = Logger.getLogger(Migration.class);

  private Connection connection;

  private File file;
  private InputStream inputStream;

  public void migrate(Connection connection, File file) throws Exception {
    this.connection = connection;
    this.file = file;
    download();
  }

  private void createInputStream() {
    try {
      inputStream = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      logger.error(e);
    }
  }

  private void download() throws Exception {

    createInputStream();

    connection.setAutoCommit(false);
    switch (Objects.requireNonNull(getFileFormat(file.getPath()))) {
      case "xml":
        Worker.getCiaWorker(connection, inputStream).execute();
        break;
      case "txt":
        Worker.getFrsWorker(connection, inputStream).execute();
    }
    connection.setAutoCommit(true);
  }

  private static String getFileFormat(String path) {
    String res = FilenameUtils.getExtension(path);
    if (res.isEmpty()) return null;
    if (res.equals("xml") || res.equals("txt")) return res;
    return getFileFormat(FilenameUtils.removeExtension(path));
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
