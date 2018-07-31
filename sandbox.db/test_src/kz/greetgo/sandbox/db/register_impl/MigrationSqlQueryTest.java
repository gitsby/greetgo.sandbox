package kz.greetgo.sandbox.db.register_impl;

import com.jcraft.jsch.ChannelSftp;
import jdk.internal.org.objectweb.asm.ClassWriter;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.tmpmodels.TmpClientDetails;
import kz.greetgo.sandbox.controller.model.tmpmodels.TmpPhone;
import kz.greetgo.sandbox.db.migration_util.*;
import kz.greetgo.sandbox.db.test.beans._develop_.MigrationWorker;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;
import kz.greetgo.sandbox.migration.core.ClientRecord;
import kz.greetgo.sandbox.migration.util.TimeUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;
import sun.nio.ch.IOUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;



public class MigrationSqlQueryTest extends ParentTestNg {

    BeanGetter<MigrationWorker> migrationWorker;
    public int portionSize = 1_000_000;
    final int MAX_STORING_ID_COUNT = 1_000_000;
    final int MAX_BATCH_SIZE = 50_000;
    final long PING_MILLIS = 2500;
    public XmlStreamParser xsp = new XmlStreamParser() ;
    public ParseJsonStream pjs = new ParseJsonStream() ;
    //    public  xsp = new XmlStreamParser() ;

    TestsBeanContainer bc = TestsBeanContainerCreator.create();
    public int showStatusPingMillis = 5000;
    private String tmpClientTable;


    public MigrationSqlQueryTest() throws Exception {
    }

    @Test
    public void connTest() throws Exception {
        bc.migrationWorker().recreateAll();
        bc.migrationWorker().tmpDbTmpConfig.get().url();
        bc.migrationWorker().tmpDbTmpConfig.get().username();
        bc.migrationWorker().tmpDbTmpConfig.get().password();

    }

    @Test
    public void cia100_000And1_000_000Input() throws Exception {
        bc.migrationWorker().recreateAll();

        SshToStream.getConnection();
        String[] dirs = {"100_000"/*,"1_000_000"*/};
        for (String dir : dirs) {
            ArrayList<String> fileNames = SshToStream.getFileNames("cia", dir);

            SshToStream.channelSftp.cd(dir);

            Insert insert = new Insert("transition_client");
            PreparedStatement ps = bc.migrationWorker()
                    .getTmpDbConnection()
                    .prepareStatement("insert into transition_client (record_data) values (?)");

//        fileNames.remove(0);
            bc.migrationWorker().setAutoCommit(false);
            for (String fileName : fileNames) {

                System.out.println("\n" + fileName + "\n");

                String bundle;
                int batchSize = 0, inserts = 0;
                long startedAt = System.nanoTime();
                try (InputStream inputStream = SshToStream.getStream(fileName)) {
                    try (BufferedReader bufferedReader = SshToStream.unzipStream(inputStream)) {
                        while ((bundle = xsp.createBundle(bufferedReader)) != null) {
                            ps.setString(1, bundle);
                            ps.addBatch();
                            batchSize++;
                            inserts++;
                            if (batchSize > MAX_BATCH_SIZE) {
                                System.out.println("gonna execute");
                                ps.executeBatch();
                                bc.migrationWorker().commit();
                                batchSize = 0;
                            }
                        }
                    }

                    System.out.println("gonna execute");
                    ps.executeBatch();
                    bc.migrationWorker().commit();
                    System.out.println("\nFILE CALLED " + fileName + " IS DONE!\n");
                    long now = System.nanoTime();
                    System.out.println(TimeUtils.showTime(now, startedAt));
                }
            }
            System.out.println("\nDONE\n");
        }

    }
    @Test
    public void frsALLInput() throws Exception {
        bc.migrationWorker().recreateAll();

        SshToStream.getConnection();
        String[] dirs = {"100_000", "1_000_000"};
        for (String dir : dirs) {
            ArrayList<String> fileNames = SshToStream.getFileNames("frs", dir);

            SshToStream.channelSftp.cd(dir);

            Insert insert = new Insert("transition_client");
            PreparedStatement ps = bc.migrationWorker()
                    .getTmpDbConnection()
                    .prepareStatement("insert into transition_transaction (record_data) values (?)");

            bc.migrationWorker().setAutoCommit(false);
            for (String fileName : fileNames) {

                System.out.println("\n" + fileName + "\n");

                String bundle;
                int batchSize = 0, inserts = 0;
                long startedAt = System.nanoTime();
                try (InputStream inputStream = SshToStream.getStream(fileName)) {
                    try (BufferedReader bufferedReader = SshToStream.unzipStream(inputStream)) {
                        while ((bundle = bufferedReader.readLine()) != null) {
                            ps.setString(1, bundle);
                            ps.addBatch();
                            batchSize++;
                            inserts++;
                            if (batchSize > MAX_BATCH_SIZE) {
                                System.out.println("gonna execute");
                                ps.executeBatch();
                                bc.migrationWorker().commit();
                                batchSize = 0;
                            }
                        }
                    }

                    System.out.println("gonna execute");
                    ps.executeBatch();
                    bc.migrationWorker().commit();
                    System.out.println("\nFILE CALLED " + fileName + " IS DONE!\n");
                    long now = System.nanoTime();
                    System.out.println(TimeUtils.showTime(now, startedAt));
                }
            }
            System.out.println("\nDONE\n");
        }

    }

    @Test
    public void takeOneAndPlaceGoodXML() throws Exception {

        Connection connection = bc.migrationWorker().getTmpDbConnection();

        bc.migrationWorker().setAutoCommit(false);

        exec("create table TMP_CLIENT (\n" +
                "  status int not null default 0,\n" +
                "  error varchar(300),\n" +
                "  number bigint not null primary key,\n" +
                "  client_id varchar(300),\n" +
                "  name varchar(300),\n" +
                "  patronymic varchar(300),\n" +
                "  surname varchar(300),\n" +
                "  birth_date date,\n" +
                "  gender varchar(6)" +
                ")");

        exec("create table TMP_CLIENT_ADDR (\n" +
                "  client_id varchar(300),\n" +
                "  street varchar(300),\n" +
                "  house varchar(300),\n" +
                "  flat varchar(300),\n" +
                "  type varchar(300)\n" +
                ")");

        exec("create table TMP_CLIENT_PHONE (\n" +
                "  client_id varchar(300),\n" +
                "  number varchar(300),\n" +
                "  type varchar(300)\n" +
                ")");

        Insert insertClient = new Insert("TMP_CLIENT");
        insertClient.field(1, "status", "?");
        insertClient.field(2, "error", "?");
        insertClient.field(3, "number", "?");
        insertClient.field(4, "client_id", "?");
        insertClient.field(5, "name", "?");
        insertClient.field(6, "patronymic", "?");
        insertClient.field(7, "surname", "?");
        insertClient.field(8, "birth_date", "?");
        insertClient.field(9, "gender", "?");

        Insert insertAddr = new Insert("TMP_CLIENT_ADDR");
        insertAddr.field(1, "client_id", "?");
        insertAddr.field(2, "street", "?");
        insertAddr.field(3, "house", "?");
        insertAddr.field(4, "flat", "?");
        insertAddr.field(5, "type", "?");


        Insert insertPhone = new Insert("TMP_CLIENT_PHONE");
        insertPhone.field(1, "client_id", "?");
        insertPhone.field(2, "number", "?");
        insertPhone.field(3, "type", "?");

        SimpleDateFormat format=  new SimpleDateFormat("yyyy-MM-dd");

        int clientInserts = 0;
        int clientAddrInserts = 0;
        int clientPhoneInserts = 0;


        long dataWidth = 0;
        try (PreparedStatement ps = connection.prepareStatement("select count(number) from transition_client")){
            try(ResultSet resultSet = ps.executeQuery()){
               while(resultSet.next())
                dataWidth = resultSet.getInt(1);
            }
        }
        System.out.println(dataWidth);
        int partSize = 500000;
        for(int offset = 0; offset<=dataWidth; offset+=partSize){
        try (PreparedStatement ps = connection.prepareStatement("select * from transition_client where status='JUST_INSERTED' limit "+String.valueOf(partSize)+" offset "+String.valueOf(offset))) {


            try(PreparedStatement forClientInsert = connection.prepareStatement(insertClient.toString());
                PreparedStatement forClientAddrInsert = connection.prepareStatement(insertAddr.toString());
                PreparedStatement forClientPhoneInsert = connection.prepareStatement(insertPhone.toString()))
            {
                try (ResultSet xmlRS = ps.executeQuery()) {
                    while (xmlRS.next()) {
                        long number = xmlRS.getLong(1);
                        Date inserted_at = xmlRS.getTime(2);
                        String record_data = xmlRS.getString(5);
                        TmpClientDetails tmpClient= xsp.createTmpClient(record_data);

                        forClientInsert.setString(1,"JUST_INSERTED");

                        forClientInsert.setLong(3,number);
                        forClientInsert.setString(4, tmpClient.id);
                        forClientInsert.setString(5, tmpClient.name);
                        forClientInsert.setString(6, tmpClient.patronymic);
                        forClientInsert.setString(7, tmpClient.surname);
                        Long date;
                        try{
                            date = format.parse(tmpClient.birthDate).getTime();
                            forClientInsert.setDate(8,new java.sql.Date(date));
                            forClientInsert.setString(1,"OK");
                            forClientInsert.setString(2,null);

                        }catch (Exception e){
                            forClientInsert.setString(2,"No birthdate");
                            forClientInsert.setString(1,"ERROR");

                            System.out.println(tmpClient);

                        }


                        forClientInsert.setString(9, tmpClient.gender);
                        forClientInsert.addBatch();
                        clientInserts++;
                        if(clientInserts>MAX_BATCH_SIZE) {
                            info("Inserted: " + clientInserts);
                            forClientInsert.executeBatch();
                            bc.migrationWorker().commit();
                            clientInserts=0;

                        }

                        forClientAddrInsert.setString(1, tmpClient.id);
                        forClientAddrInsert.setString(2, tmpClient.tmpFacAddress.street);
                        forClientAddrInsert.setString(3, tmpClient.tmpFacAddress.house);
                        forClientAddrInsert.setString(4, tmpClient.tmpFacAddress.flat);
                        forClientAddrInsert.setString(5, "FACT");
                        forClientInsert.addBatch();
                        clientAddrInserts++;
                        if(clientAddrInserts>MAX_BATCH_SIZE) {
                            info("Inserted: " + clientAddrInserts );
                            forClientAddrInsert.executeBatch();
                            bc.migrationWorker().commit();
                            clientAddrInserts=0;
                        }

                        forClientAddrInsert.setString(1, tmpClient.id);
                        forClientAddrInsert.setString(2, tmpClient.tmpRegAddress.street);
                        forClientAddrInsert.setString(3, tmpClient.tmpRegAddress.house);
                        forClientAddrInsert.setString(4, tmpClient.tmpRegAddress.flat);
                        forClientAddrInsert.setString(5, "REG");
                        forClientInsert.addBatch();
                        clientAddrInserts++;



                        for(TmpPhone phone: tmpClient.tmpPhones){
                            forClientPhoneInsert.setString(1,tmpClient.id);
                            forClientPhoneInsert.setString(2,phone.number);
                            forClientPhoneInsert.setString(3,phone.phoneType);
                            forClientPhoneInsert.addBatch();
                            clientPhoneInserts++;

                            if(clientPhoneInserts>MAX_BATCH_SIZE) {
                                info("Inserted: " + clientPhoneInserts );
                                forClientPhoneInsert.executeBatch();
                                bc.migrationWorker().commit();
                                clientPhoneInserts=0;

                            }

                        }


                    }

                }
                forClientInsert.executeBatch();
                forClientPhoneInsert.executeBatch();
                forClientAddrInsert.executeBatch();
                bc.migrationWorker().commit();
                info("INSERTED "+offset+ " records from "+dataWidth);
            }
        }
        }

        bc.migrationWorker().setAutoCommit(false);

    }


    @Test
    public void takeOneAndPlaceGoodJSON() throws Exception {
        Connection connection = bc.migrationWorker().getTmpDbConnection();
        try (PreparedStatement ps = connection.prepareStatement("" +
                "select * from transition_transaction where status='JUST_INSERTED' limit 300")) {
            try (ResultSet xmlRS = ps.executeQuery()) {
                int selections = 0;
                while (xmlRS.next()){
                    long number = xmlRS.getLong(1);
                    Date inserted_at = xmlRS.getTime(2);
                    String status = xmlRS.getString(3);
                    String error = xmlRS.getString(4);
                    String record_data = xmlRS.getString(5);
                    System.out.println(pjs.parse(record_data));
                    selections++;
                    if(selections==300){
                        break;
                    }
                };

            }
        }
    }



//    @Test
//    public void migrate() throws Exception {
//        long startedAt = System.nanoTime();
//        bc.migrationWorker().getTmpDbConnection();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        Date nowDate = new Date();
//        tmpClientTable = "cia_migration_client_" + sdf.format(nowDate);
//        info("TMP_CLIENT = " + tmpClientTable);
//
//        exec("create table TMP_CLIENT (\n" +
//                "  status int not null default 0,\n" +
//                "  error varchar(300),\n" +
//                "  number bigint not null primary key,\n" +
//                "  cia_id varchar(100) not null,\n" +
//                "  client_id int8,\n" +
//                "  name varchar(300),\n" +
//                "  patronymic varchar(300),\n" +
//                "  surname varchar(300),\n" +
//                "  birth_date date,\n" +
//                "  gender varchar(6)" +
//                ")");
//
//        exec("create table TMP_CLIENT_ADDR (\n" +
//                "  client_id int8,\n" +
//                "  street varchar(300),\n" +
//                "  house varchar(300),\n" +
//                "  flat varchar(300),\n" +
//                "  type varchar(300)\n" +
//                ")");
//
//        exec("create table TMP_CLIENT_PHONE (\n" +
//                "  client_id int8,\n" +
//                "  number varchar(300),\n" +
//                "  type varchar(300)\n" +
//                ")");
//
//        int portionSize = download();
//
//        {
//            long now = System.nanoTime();
//            info("Downloaded of portion " + portionSize + " finished for " + TimeUtils.showTime(now, startedAt));
//        }
//
//        if (portionSize == 0) return ;
//
//
//
//        try(PreparedStatement ps = bc.migrationWorker()
//                .getTmpDbConnection()
//                .prepareStatement(
//                        "select * from transition_client")) {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    private int download() throws Exception {
//
//        final AtomicBoolean working = new AtomicBoolean(true);
//        final AtomicBoolean showStatus = new AtomicBoolean(false);
//
//        final Thread see = new Thread(() -> {
//
//            while (working.get()) {
//
//                try {
//                    Thread.sleep(showStatusPingMillis);
//                } catch (InterruptedException e) {
//                    break;
//                }
//
//                showStatus.set(true);
//
//            }
//
//        });
//        see.start();
//
//        try (PreparedStatement ciaPS = bc.migrationWorker().connection.prepareStatement(
//
//                "select * from transition_client where status='JUST_INSERTED' order by number limit ?")) {
//
//            info("Prepared statement for : select * from transition_client");
//
//            ciaPS.setInt(1, portionSize);
//
//            Insert insertClient = new Insert("TMP_CLIENT");
//            insertClient.field(1, "number", "?");
//            insertClient.field(2, "cia_id", "?");
//            insertClient.field(3, "surname", "?");
//            insertClient.field(4, "name", "?");
//            insertClient.field(5, "patronymic", "?");
//            insertClient.field(6, "birth_date", "?");
//            insertClient.field(7, "client_id", "?");
//            insertClient.field(8, "gender", "?");
//
//            Insert insertAddr = new Insert("TMP_CLIENT_PHONE");
//            insertAddr.field(1, "number", "?");
//            insertAddr.field(2, "cia_id", "?");
//            insertAddr.field(3, "surname", "?");
//            insertAddr.field(4, "name", "?");
//            insertClient.field(5, "patronymic", "?");
//            insertClient.field(6, "birth_date", "?");
//            insertClient.field(7, "client_id", "?");
//            insertClient.field(8, "gender", "?");
//
//
//            Insert insertPhone = new Insert("TMP_CLIENT_PHONE");
//
//
//            operConnection.setAutoCommit(false);
//            try (PreparedStatement operPS = operConnection.prepareStatement(r(insert.toString()))) {
//
//                try (ResultSet ciaRS = ciaPS.executeQuery()) {
//
//                    info("Got result set for : select * from transition_client");
//
//                    int batchSize = 0, recordsCount = 0;
//
//                    long startedAt = System.nanoTime();
//
//                    while (ciaRS.next()) {
//                        ClientRecord r = new ClientRecord();
//                        r.number = ciaRS.getLong("number");
//                        r.parseRecordData(ciaRS.getString("record_data"));
//
//                        operPS.setLong(1, r.number);
//                        operPS.setString(2, r.id);
//                        operPS.setString(3, r.surname);
//                        operPS.setString(4, r.name);
//                        operPS.setString(5, r.patronymic);
//                        operPS.setDate(6, r.birthDate);
//
//                        operPS.addBatch();
//                        batchSize++;
//                        recordsCount++;
//
//                        if (batchSize >= downloadMaxBatchSize) {
//                            operPS.executeBatch();
//                            operConnection.commit();
//                            batchSize = 0;
//                        }
//
//                        if (showStatus.get()) {
//                            showStatus.set(false);
//
//                            long now = System.nanoTime();
//                            info(" -- downloaded records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
//                                    + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
//                        }
//
//                    }
//
//                    if (batchSize > 0) {
//                        operPS.executeBatch();
//                        operConnection.commit();
//                    }
//
//                    {
//                        long now = System.nanoTime();
//                        info("TOTAL Downloaded records " + recordsCount + " for " + TimeUtils.showTime(now, startedAt)
//                                + " : " + TimeUtils.recordsPerSecond(recordsCount, now - startedAt));
//                    }
//
//                    return recordsCount;
//                }
//            } finally {
//                operConnection.setAutoCommit(true);
//                working.set(false);
//                see.interrupt();
//            }
//        }
//    }


    private void exec(String sql) throws SQLException {
//        String executingSql = r(sql);

        long startedAt = System.nanoTime();
        try (Statement statement = bc.migrationWorker().connection.createStatement()) {
            int updates = statement.executeUpdate(sql);
            info("Updated " + updates
                    + " records for " + TimeUtils.showTime(System.nanoTime(), startedAt)
                    + ", EXECUTED SQL : " + sql);
        } catch (SQLException e) {
            info("ERROR EXECUTE SQL for " + TimeUtils.showTime(System.nanoTime(), startedAt)
                    + ", message: " + e.getMessage() + ", SQL : " + sql);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String r(String sql) {
//        sql = sql.replaceAll("TMP_CLIENT", tmpClientTable);
//        return sql;
//    }

    private void info(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
    }
}


