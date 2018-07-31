package kz.greetgo.sandbox.db.migration_util;

import com.jcraft.jsch.*;
import kz.greetgo.depinject.core.Bean;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Collectors;

//@Bean
public class SshToStream {

    static public ChannelSftp channelSftp;
    static public Session session;
    static public InputStream is;

    public static ChannelSftp getConnection() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession("pigeon","localhost", 2222);
        session.setPassword("greetgo");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        System.out.println("set ssh conn");
        channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        return channelSftp;
    }



    public static ArrayList<String> getFileNames(String type, String path) throws SftpException {

        channelSftp.cd(channelSftp.getHome());
        channelSftp.cd(path);

        Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.bz2");

         ArrayList<String> files = list.stream()
                .filter( file -> file.getFilename()
                        .contains(type)).map(ChannelSftp.LsEntry::getFilename)
                .collect(Collectors.toCollection(ArrayList::new));
        channelSftp.cd("../");
        channelSftp.cd(channelSftp.getHome());
        return files;
    }

    public static InputStream getStream(String fileName) throws SftpException {
        return channelSftp.get(fileName);
    }

    public static BufferedReader unzipStream(InputStream is) throws SftpException, IOException, JSchException {

            BufferedInputStream bis = new BufferedInputStream(is);
            BZip2CompressorInputStream b2cis = new BZip2CompressorInputStream(bis);
            ArchiveInputStream tis = new TarArchiveInputStream(b2cis);
            ArchiveEntry archiveEntry = null;

            while ((archiveEntry = tis.getNextEntry()) != null) {
                if (archiveEntry.isDirectory()) continue;
                BufferedReader br;
                InputStream tmpIn = new SizeLimitInputStream(tis, archiveEntry.getSize());
                br = new BufferedReader(new InputStreamReader(tmpIn));
                return br;
            }
        return null;
    }
}
