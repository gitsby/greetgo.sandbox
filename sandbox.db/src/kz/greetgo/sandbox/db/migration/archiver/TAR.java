package kz.greetgo.sandbox.db.migration.archiver;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TAR {

  public static void main(String[] args) throws IOException {
//
//    File file = new File("C:\\Programs\\from_cia_2018-02-21-154929-1-300.xml.tar.bz2");
//
//    InputStream stream = new FileInputStream(file);
//    TAR.extractTarGZ(stream);

    FileInputStream in = new FileInputStream("C:\\Programs\\from_cia_2018-02-21-154929-1-300.xml.tar.bz2");
    FileOutputStream out = new FileOutputStream("C:\\Programs\\archive.tar");
    BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
    final byte[] buffer = new byte[1024];
    int n = 0;
    while (-1 != (n = bzIn.read(buffer))) {
      out.write(buffer, 0, n);
    }
    out.close();
    bzIn.close();
    TAR.unTarFile(new File("C:\\Programs\\archive.tar"), new File("C:\\Programs\\"));
  }

  public static void unTarFile(File tarFile, File destFile) throws IOException {
    FileInputStream fis = new FileInputStream(tarFile);
    TarArchiveInputStream tis = new TarArchiveInputStream(fis);
    TarArchiveEntry tarEntry = null;

    // tarIn is a TarArchiveInputStream
    while ((tarEntry = tis.getNextTarEntry()) != null) {
      File outputFile = new File(destFile + File.separator + tarEntry.getName());

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
