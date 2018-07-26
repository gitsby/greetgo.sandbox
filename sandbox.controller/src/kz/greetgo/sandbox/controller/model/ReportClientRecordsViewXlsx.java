package kz.greetgo.sandbox.controller.model;


import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ReportClientRecordsViewXlsx implements ReportClientRecordsView {

    private final OutputStream out;
    private Xlsx xlsx;
    private Sheet sheet;
    private String user;


    public ReportClientRecordsViewXlsx(OutputStream out){
        this.out=out;
    }


    @Override
    public void start(String user, Date reportDate)throws Exception{
        xlsx = new Xlsx();
        this.user=user;
        sheet =xlsx.newSheet(true);

        sheet.row().start();

        sheet.cellStr(1, "Report from:");
        SimpleDateFormat  sdf = new SimpleDateFormat("dd/MM/yyyy");
        sheet.cellStr(2, sdf.format(reportDate));

        sheet.row().finish();

        sheet.skipRow();
        sheet.skipRow();

        sheet.row().start();
        sheet.cellStr(1,"#");
        sheet.cellStr(2,"Fullname");
        sheet.cellStr(3,"Age");
        sheet.cellStr(4,"Charm");
        sheet.cellStr(5,"Total balance");
        sheet.cellStr(6,"Max balance");
        sheet.cellStr(7,"Min balance");
        sheet.row().finish();
    }


    @Override
    public void append(ClientRecord clientRecord, int index)throws Exception{
        sheet.row().start();
        sheet.cellInt(1,index);
        sheet.cellStr(2, clientRecord.fullName);
        sheet.cellInt(3,(int) ((Instant.now().toEpochMilli()- clientRecord.age)/(315360000))/100);
        sheet.cellStr(4, clientRecord.charm);
        sheet.cellDouble(5, clientRecord.totalBalance);
        sheet.cellDouble(6, clientRecord.maxBalance);
        sheet.cellDouble(7, clientRecord.minBalance);
        sheet.row().finish();
    }


    @Override
    public void finish()throws Exception{
        sheet.skipRow();
        sheet.skipRow();

        sheet.row().start();
        sheet.cellStr(1, "Made by "+this.user);
        sheet.row().finish();

        xlsx.complete(out);
    }

    public static void main(String[] args) throws Exception{
        OutputStream outf = new FileOutputStream(new File("D:/greetgonstuff/greetgo.sandbox/reports/test.xlsx"));
        ReportClientRecordsViewXlsx x = new ReportClientRecordsViewXlsx(outf);
        x.start("badboi", new Date());
        for (int i = 0; i <5 ; i++) {
            ClientRecord clientRecord =new ClientRecord();
            clientRecord.fullName="asd";
            clientRecord.charm="asd";
            clientRecord.age=2140000000;
            clientRecord.totalBalance=228;
            clientRecord.minBalance=228;
            clientRecord.maxBalance=228;
            x.append(clientRecord,i);
        }
        x.finish();
    }

    }

