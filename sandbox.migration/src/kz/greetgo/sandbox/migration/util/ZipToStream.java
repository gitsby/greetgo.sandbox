package kz.greetgo.sandbox.migration.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.IOUtils;
//import kz.greetgo.mvc.interfaces.RequestTunnel;

import java.io.*;



public class ZipToStream {



    public BufferedReader getUnzipped(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        BZip2CompressorInputStream b2cis = new BZip2CompressorInputStream(bis);
        ArchiveInputStream tis = new TarArchiveInputStream(b2cis);
        ArchiveEntry archiveEntry = null;

        while((archiveEntry=tis.getNextEntry())!=null){
            if (archiveEntry.isDirectory()) continue;

            InputStream tmpIn = new SizeLimitInputStream(tis, archiveEntry.getSize());

            BufferedReader br = new BufferedReader(new InputStreamReader(tmpIn));

            return br;

        }

        return null;
    }

    public static void main(String[] args) throws IOException, CompressorException {
        File file = new File("D:\\greetgonstuff\\learn.migration\\src\\myzip.tar.bz2");
        FileInputStream fis  = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BZip2CompressorInputStream b2cis = new BZip2CompressorInputStream(bis);
        ArchiveInputStream tis = new TarArchiveInputStream(b2cis);

        ArchiveEntry archiveEntry = null;
        while((archiveEntry=tis.getNextEntry())!=null){
            if (archiveEntry.isDirectory()) continue;
            InputStream tmpIn = new SizeLimitInputStream(tis, archiveEntry.getSize());
            BufferedReader br = new BufferedReader(new InputStreamReader(tmpIn));

            int nums = 0;
            while (br.ready()) {
                if (nums < 10) {
                    System.out.println(br.readLine());
                    System.out.println("==================");
                    nums++;
                }

            }        }

    }



}
