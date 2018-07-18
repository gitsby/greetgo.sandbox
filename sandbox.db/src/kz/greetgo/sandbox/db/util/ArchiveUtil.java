package kz.greetgo.sandbox.db.util;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class ArchiveUtil {

  private static Logger logger = Logger.getLogger(ArchiveUtil.class);

  public static File unzip(File file) throws IOException, InterruptedException {
    return unarchiveFile(file);
  }

  private static File unarchiveFile(File file) throws IOException, InterruptedException {
    logger.info("Begin unzip "+file.getName()+"...");
    logger.info("Create unzip folder.");
    File unzipFolder = new File(file.getParent()+"/unzipFolder");
    unzipFolder.mkdir();
    logger.info("Unzip folder created.");

    logger.info("Create builder.");
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("tar", "-xvzf", file.getPath(), "-C", unzipFolder.getPath());
    builder.inheritIO();
    logger.info("Builder created.");

    logger.info("Start unzip process...");
    long startTime = System.nanoTime();
    Process process = builder.start();
    int exitStatus = process.waitFor();
    if (exitStatus != 0) throw new RuntimeException("Error unzip file " + file + " with exit status " + exitStatus);
    process.destroy();
    logger.info("End unzip file: "+(System.nanoTime()-startTime)+" ns");

    logger.info("Replace file to tmp.");
    File inDirFile = getFile(unzipFolder);

    File newFile = new File(file.getParent()+"/"+inDirFile.getName());
    newFile.createNewFile();

    inDirFile.renameTo(newFile);
    logger.info("File " + inDirFile.getName() + " replaced.");

    logger.info("Delete zip file and unzip folder.");
    file.delete();
    FileUtils.deleteDirectory(unzipFolder);

    return newFile;
  }

  private static File getFile(File file) {
    if (file.getName().endsWith("xml") || file.getName().endsWith("txt")) return file;
    File[] children = file.listFiles();
    if (children == null) return null;
    for (File child : children) {
      File res = getFile(child);
      if (res != null) return res;
    }
    return null;
  }
}