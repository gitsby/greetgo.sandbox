package kz.greetgo.sandbox.controller.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ReportTableViewPdf implements ReportTableView {

    private final OutputStream out;
    private String user;
    private Document document;
    private Date date;

    public ReportTableViewPdf(OutputStream out){
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
    public void append(TableModel tableModel, int index)throws Exception{
        PdfPTable row = new PdfPTable(7);
        row.addCell(index+"");
        row.addCell(tableModel.fullName);
        row.addCell(((Instant.now().toEpochMilli()-tableModel.age)/(315360000))/100 + "");
        row.addCell(tableModel.charm);
        row.addCell(tableModel.totalBalance + "");
        row.addCell(tableModel.maxBalance + "");
        row.addCell(tableModel.minBalance + "");
        document.add(row);
    }


    @Override
    public void finish()throws Exception{

        document.add(new Phrase("Made by "+this.user));
        document.close();

    }

    public static void main(String[] args) throws Exception{
        OutputStream outf = new FileOutputStream(new File("D:/greetgonstuff/greetgo.sandbox/reports/test.pdf"));
        ReportTableViewPdf x = new ReportTableViewPdf(outf);
        x.start("badboi", new Date());
        for (int i = 0; i <5 ; i++) {
            TableModel tableModel=new TableModel();
            tableModel.fullName="asd";
            tableModel.charm="asd";
            tableModel.age=2140000000;
            tableModel.totalBalance=228;
            tableModel.minBalance=228;
            tableModel.maxBalance=22;
            x.append(tableModel,i);
        }
        x.finish();
    }

}
