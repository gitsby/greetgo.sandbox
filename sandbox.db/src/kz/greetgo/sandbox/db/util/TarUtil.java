package kz.greetgo.sandbox.db.util;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;

public class TarUtil {

  private static Logger logger = Logger.getLogger("archiver");

  public static File untar(File file) throws IOException, InterruptedException {
    return untarFile(file);
  }

  private static File untarFile(File file) throws IOException, InterruptedException {
    logger.info("Begin unzip " + file.getName() + "...");
    logger.info("Create tmp unzip folder.");
    File tmpUnzipFolder = createFolder(file.getParent()+"/tmp_unzip_folder");
    logger.info("Unzip folder created.");

    startUnzipProcess(createUnzipProcessBuilder(file, tmpUnzipFolder));

    logger.info("Replace file to tmp.");
    File inDirFile = getInnerFile(tmpUnzipFolder);

    File newFile = createFile(file.getParent()+"/"+Objects.requireNonNull(inDirFile).getName());

    //noinspection ResultOfMethodCallIgnored
    inDirFile.renameTo(newFile);
    logger.info("File " + inDirFile.getName() + " replaced.");

    logger.info("Delete zip file and unzip folder.");
    //noinspection ResultOfMethodCallIgnored
    file.delete();
    FileUtils.deleteDirectory(tmpUnzipFolder);

    return newFile;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static File createFile(String path) throws IOException {
    File file = new File(path);
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      file.createNewFile();
    }
    return file;
  }

  private static File createFolder(String path) {
    File dir = new File(path);
    if (dir.isFile()) throw new RuntimeException(new FileAlreadyExistsException(path+" already exist!"));
    //noinspection ResultOfMethodCallIgnored
    dir.mkdir();
    return dir;
  }

  private static File getInnerFile(File file) {
    if (file.getName().endsWith("xml") || file.getName().endsWith("txt")) return file;
    File[] children = file.listFiles();
    if (children == null) return null;
    for (File child : children) {
      File res = getInnerFile(child);
      if (res != null) return res;
    }
    throw new RuntimeException(new FileNotFoundException());
  }

  private static void startUnzipProcess(ProcessBuilder builder) throws IOException, InterruptedException {
    logger.info("Start unzip process...");
    long startTime = System.nanoTime();
    Process process = builder.start();
    int exitStatus = process.waitFor();
    if (exitStatus != 0) throw new RuntimeException("Error unzip file !");
    process.destroy();
    logger.info("End unzip file: "+(System.nanoTime()-startTime)+" ns");
  }

  private static ProcessBuilder createUnzipProcessBuilder(File f1, File f2) {
    logger.info("Create builder.");
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("tar", "-xvzf", f1.getPath(), "-C", f2.getPath());
    builder.inheritIO();
    logger.info("Builder created.");
    return builder;
  }
}