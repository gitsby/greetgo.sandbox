package kz.greetgo.sandbox.db.core;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.db.worker.Worker;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import liquibase.util.file.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Bean
public class Migration implements Closeable {

  private static Logger logger = Logger.getLogger(Migration.class);

  private Connection connection;

  private File file;
  private InputStream inputStream;

  public File migrate(Connection connection, File file) throws IOException, SQLException {
    this.connection = connection;
    this.file = file;
    return download();
  }

  private void createInputStream() throws FileNotFoundException {
    inputStream = new FileInputStream(file);
  }

  private File download() throws IOException, SQLException {

    File errorsFile;
    createInputStream();

    connection.setAutoCommit(false);
    switch (Objects.requireNonNull(getFileFormat(file.getPath()))) {
      case "xml":
        try(CIAWorker worker = Worker.getCiaWorker(connection, inputStream)) { errorsFile = worker.execute(); }
        break;
      case "txt":
        try(FRSWorker worker = Worker.getFrsWorker(connection, inputStream)) { errorsFile = worker.execute(); }
        break;
      default:
        throw new RuntimeException("File format not support!");
    }
    connection.setAutoCommit(true);

    return errorsFile;
  }

  private String getFileFormat(String path) {
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
