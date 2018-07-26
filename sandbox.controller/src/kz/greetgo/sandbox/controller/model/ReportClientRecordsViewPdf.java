package kz.greetgo.sandbox.controller.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ReportClientRecordsViewPdf implements ReportClientRecordsView {

    private final OutputStream out;
    private String user;
    private Document document;
    private Date date;

    public ReportClientRecordsViewPdf(OutputStream out){
        this.out=out;
    }


    @Override
    public void start(String user, Date reportDate) throws Exception{

        this.user = user;
        this.date = reportDate;
        document = new Document();
        PdfWriter.getInstance(document,out);

        if (!document.isOpen()) document.open();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        document.add(new Paragraph("Report from: " + sdf.format(reportDate)));
        document.add(new Phrase(""));
        PdfPTable row = new PdfPTable(7);

        row.addCell("#");
        row.addCell("Fullname");
        row.addCell("Age");
        row.addCell("Charm");
        row.addCell("Total balance");
        row.addCell("Max balance");
        row.addCell("Min balance");
        document.add(row);
    }


    @Override
    public void append(ClientRecord clientRecord, int index)throws Exception{
        PdfPTable row = new PdfPTable(7);
        row.addCell(index+"");
        row.addCell(clientRecord.fullName);
        row.addCell(((Instant.now().toEpochMilli()- clientRecord.age)/(315360000))/100 + "");
        row.addCell(clientRecord.charm);
        row.addCell(clientRecord.totalBalance + "");
        row.addCell(clientRecord.maxBalance + "");
        row.addCell(clientRecord.minBalance + "");
        document.add(row);
    }


    @Override
    public void finish()throws Exception{
        document.add(new Phrase("Made by "+this.user));
        document.close();

    }

    public static void main(String[] args) throws Exception{
        OutputStream outf = new FileOutputStream(new File("D:/greetgonstuff/greetgo.sandbox/reports/test.pdf"));
        ReportClientRecordsViewPdf x = new ReportClientRecordsViewPdf(outf);
        x.start("badboi", new Date());
        for (int i = 0; i <1000000 ; i++) {
            ClientRecord clientRecord =new ClientRecord();
            clientRecord.fullName="asd";
            clientRecord.charm="asd";
            clientRecord.age=2140000000;
            clientRecord.totalBalance=228;
            clientRecord.minBalance=228;
            clientRecord.maxBalance=22;
            x.append(clientRecord,i);
        }
        x.finish();
    }

}
