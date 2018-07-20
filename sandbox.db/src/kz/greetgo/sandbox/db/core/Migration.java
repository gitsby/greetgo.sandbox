package kz.greetgo.sandbox.db.core;

import kz.greetgo.sandbox.db.worker.Worker;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.sandbox.db.worker.impl.FRSWorker;
import liquibase.util.file.FilenameUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class Migration implements Closeable {

  private Connection connection;

  private File file;
  private InputStream inputStream;

  public File migrate(Connection connection, File file) throws IOException {
    this.connection = connection;
    this.file = file;
    return download();
  }

  private File download() throws IOException {
    File errorsFile = null;
    inputStream = new FileInputStream(file);
    switch (Objects.requireNonNull(getFileFormat(file.getPath()))) {
      case "xml":
        try (CIAWorker worker = Worker.getCiaWorker(connection, inputStream)) { errorsFile = worker.execute(); }
        break;
      case "txt":
        try (FRSWorker worker = Worker.getFrsWorker(connection, inputStream)) { errorsFile = worker.execute(); }
        break;
    }
    return errorsFile;
  }

  private String getFileFormat(String path) {
    String extension = FilenameUtils.getExtension(path);
    if (extension.isEmpty()) throw new RuntimeException("File format not supported!");
    if (extension.equals("xml") || extension.equals("txt")) return extension;
    return getFileFormat(FilenameUtils.removeExtension(path));
  }

  @Override
  public void close() {
    try {
      inputStream.close();
      if (!connection.isClosed()) connection.close();
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
