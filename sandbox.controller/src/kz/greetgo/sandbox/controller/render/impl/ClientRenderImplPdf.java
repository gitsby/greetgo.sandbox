package kz.greetgo.sandbox.controller.render.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@Bean
public class ClientRenderImplPdf implements ClientRender {

  private OutputStream out;
  private Document document;
  private PdfPTable table;
  private PdfWriter pdfWriter;

  public ClientRenderImplPdf(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String name, Date createdDate) {
    document = new Document(PageSize.A4, 50,50,50,50);
    instance(document);
    document.open();
    setHeader(name, createdDate);
  }

  private void instance(Document document) {
    try {
      pdfWriter = PdfWriter.getInstance(document, out);
    } catch (Exception e) {
      // FIXME: 7/11/18 Проглатываение ошибки - неправильно
      e.getMessage();
    }
  }

  private void setHeader(String name, Date createdDate) {
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    Paragraph p = new Paragraph(String.format("Название: %s\nСоздано от: %s\n\n", name, format.format(createdDate)), getFont(BaseColor.BLACK, 10));
    try {
      document.add(p);
    } catch (DocumentException e) {
      e.printStackTrace();
    }

    table = new PdfPTable(8);
    table.setWidthPercentage(100);
    addTableHeader(table);
  }

  @Override
  public void append(ClientRecord record) {
    addRows(table, record);
  }

  private void addTableHeader(PdfPTable table) {
    Stream.of("ID", "SURNAME", "NAME", "PATRONYMIC", "AGE", "MIDDLE BALANCE", "MAX BALANCE", "MIN BALANCE")
      .forEach(columnTitle -> {
        PdfPCell header = getCell();
        header.setBackgroundColor(new BaseColor(46, 77,132));
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(columnTitle, getFont(BaseColor.WHITE, 7)));
        table.addCell(header);
      });
  }

  private Font getFont(BaseColor textColor, int size) {
    Font font = FontFactory.getFont("/Library/Fonts/Arial.ttf", BaseFont.IDENTITY_H, true);
    font.setColor(textColor);
    font.setSize(size);
    font.setStyle("bold");
    return font;
  }

  private void addRows(PdfPTable table, ClientRecord cr) {
    BaseColor bColor = getBackgroundColor();
    Stream.of(String.valueOf(cr.id), cr.surname, cr.name, cr.patronymic, String.valueOf(cr.age),
      String.valueOf(cr.middle_balance), String.valueOf(cr.max_balance), String.valueOf(cr.min_balance))
      .forEach(columnTitle -> {
        PdfPCell cell = getCell();
        cell.setBackgroundColor(bColor);
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(columnTitle, getFont(BaseColor.BLACK, 7)));
        table.addCell(cell);
      });
  }

  private static int color = 0;
  private BaseColor getBackgroundColor() {
    if (color++%2==0) return new BaseColor(255, 255,255);
    else return new BaseColor(240,240,240);
  }

  private PdfPCell getCell() {
    PdfPCell cell = new PdfPCell();
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_CENTER);
    return cell;
  }

  @Override
  public void finish() {
    try {
      document.add(table);
      document.close();
      pdfWriter.close();
      out.close();
    } catch (Exception e) {
      // FIXME: 7/11/18 Нужно перечилисть все места где есть printStackTrace?!
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    String fileName = "TEST.pdf";
    ClientRenderImplPdf asd = new ClientRenderImplPdf(new FileOutputStream(new File(System.getProperty("user.home")+"/Desktop/"+fileName)));
    asd.start(RND.str(10), new Date());
    for (int i=0;i<30; i++) asd.append(getRandomClientRecord(i));
    asd.finish();
  }

  private static ClientRecord getRandomClientRecord(int i) {
    ClientRecord row = new ClientRecord();
    row.id=i;
    row.surname = RND.str(10);
    row.name = RND.str(10);
    row.patronymic = RND.str(10);
    row.age = RND.plusInt(60);
    row.middle_balance = RND.plusInt(10000);
    row.max_balance = RND.plusInt(10000);
    row.min_balance = RND.plusInt(10000);
    return row;
  }
}