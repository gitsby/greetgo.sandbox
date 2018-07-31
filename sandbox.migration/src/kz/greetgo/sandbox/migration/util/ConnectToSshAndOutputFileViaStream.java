package kz.greetgo.sandbox.migration.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;


public class ConnectToSshAndOutputFileViaStream {
    public static void main(String[] args) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession("pigeon","localhost", 2222);
        session.setPassword("greetgo");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();

        String[] dirs = {"1_000_000","100_000"};

//        channelSftp.cd("100_000");
//        printContents(channelSftp,"from_cia_2018-02-21-154532-1-300.xml.tar.bz2");

    for (String dir: dirs) {
        channelSftp.cd(dir);
        prettyPrint(dir);
        Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*.bz2");
        for(ChannelSftp.LsEntry entry : list) {
            String fileName = entry.getFilename();
            String chars[] = fileName.split("\\.");
            if(chars[0].contains("from_cia")){
                prettyPrint("from_cia||xml");
                printContents(channelSftp,fileName);
            }else if(chars[0].contains("from_frs")){
                prettyPrint("from_frs||json");
                printContents(channelSftp,fileName);
            }
        }
        channelSftp.cd("../");
    }

}

    private static void prettyPrint(String data) {
        System.out.println("================================================");
        System.out.println(data);
        System.out.println("================================================");

    }
    private static void printContents(ChannelSftp channelSftp, String fileName) throws SftpException, IOException {
        ZipToStream  z2s = new ZipToStream();
        InputStream stream = channelSftp.get(fileName);

//        try {
            BufferedReader br = new BufferedReader(z2s.getUnzipped(stream));
            int nums = 0;
//            DONE:
            while (br.ready()) {
//                if (nums < 100) {
                    System.out.println(br.readLine());
//                    nums++;
//                }else{
//                    break DONE;
//                }

            }
//        }finally {
//            stream.close();
//        }
        Scanner scan=new Scanner(System.in);
        scan.nextLine();

    }
}
