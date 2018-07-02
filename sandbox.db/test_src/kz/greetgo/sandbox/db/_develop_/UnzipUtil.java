package kz.greetgo.sandbox.db._develop_;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnzipUtil {

  public static File unzip(File file) throws Exception {
    return unarchiveFile(file);
  }

  private static File unarchiveFile(File file) throws Exception {
    info("Begin unzip "+file.getName()+"...");
    info("Create unzip folder.");
    File unzipFolder = new File(file.getParent()+"/unzipFolder");
    unzipFolder.mkdir();
    info("Unzip folder created.");

    info("Create builder.");
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("tar", "-xvzf", file.getPath(), "-C", unzipFolder.getPath());
    builder.inheritIO();
    info("Builder created.");

    info("Start unzip process...");
    long startTime = System.nanoTime();
    Process process = builder.start();
    int exitStatus = process.waitFor();
    if (exitStatus != 0) throw new RuntimeException("Error unzip file " + file + " with exit status " + exitStatus);
    process.destroy();
    info("End unzip file: "+(System.nanoTime()-startTime)+" ns");

    info("Replace file to tmp.");
    File inDirFile = getFile(unzipFolder);

    File newXml = new File(file.getParent()+"/"+inDirFile.getName());
    newXml.createNewFile();

    inDirFile.renameTo(newXml);
    info("File " + inDirFile.getName() + " replaced.");

    info("Delete zip file and unzip folder.");
    file.delete();
    FileUtils.deleteDirectory(unzipFolder);

    return newXml;
  }

  private static File getFile(File file) {
    if (file.getName().endsWith("xml") || file.getName().endsWith("json_row")) return file;
    File[] children = file.listFiles();
    if (children == null) return null;
    for (File child : children) {
      File res = getFile(child);
      if (res != null) return res;
    }
    return null;
  }

  private static void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + UnzipUtil.class.getSimpleName() + "] " + message);
  }
}