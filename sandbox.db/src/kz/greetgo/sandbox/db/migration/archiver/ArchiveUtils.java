package kz.greetgo.sandbox.db.migration.archiver;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArchiveUtils {

  public static void main(String[] args) throws IOException {
    ArchiveUtils.extract("from_cia_2018-02-21-154955-5-1000000.xml.tar.bz2");
  }

  public static void extract(String file) throws IOException {
    ArchiveUtils.extractBZ2File("build/" + file);
    ArchiveUtils.unTarFile("build/" + file.replace(".bz2", ""));
  }

  public static void extractBZ2File(String file) throws IOException {
    FileInputStream in = new FileInputStream(file);
    FileOutputStream out = new FileOutputStream(file.replace(".bz2", ""));

    BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);

    final byte[] buffer = new byte[8024];
    int n = 0;
    while (-1 != (n = bzIn.read(buffer))) {
      out.write(buffer, 0, n);
    }

    out.close();
  }

  public static void unTarFile(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(new File(fileName));
    TarArchiveInputStream tis = new TarArchiveInputStream(fis);
    TarArchiveEntry tarEntry = null;

    // tarIn is a TarArchiveInputStream
    while ((tarEntry = tis.getNextTarEntry()) != null) {
      File outputFile = new File(fileName.replace(".tar", "") + File.separator + tarEntry.getName());

      if (tarEntry.isDirectory()) {

        System.out.println("outputFile Directory ---- "
          + outputFile.getAbsolutePath());
        if (!outputFile.exists()) {
          outputFile.mkdirs();
        }
      } else {
        //File outputFile = new File(destFile + File.separator + tarEntry.getName());
        System.out.println("outputFile File ---- " + outputFile.getAbsolutePath());
        outputFile.getParentFile().mkdirs();
        //outputFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(outputFile);
        IOUtils.copy(tis, fos);
        fos.close();
      }
    }
    tis.close();
  }
}
