package kz.greetgo.sandbox.db.render_impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.stream.Stream;

public class ClientRenderImplPdf implements ClientRender {

  private OutputStream out;
  private Document document;

  public ClientRenderImplPdf(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String name, Date createdDate) {
    Document document = new Document();
    PdfWriter writer = null;
    try {
      writer = PdfWriter.getInstance(document, out);
    } catch (DocumentException e) {
      e.printStackTrace();
    }

    document.open();
    Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
    Chunk chunk = new Chunk("Hello World", font);

    try {
      document.add(chunk);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    document.close();
    writer.close();
  }

  private void addTableHeader(PdfPTable table) {
    Stream.of("column header 1", "column header 2", "column header 3")
      .forEach(columnTitle -> {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(columnTitle));
        table.addCell(header);
      });
  }

  @Override
  public void append(ClientRow clientRow) {

  }

  private void addRows(ClientRow clientRow, PdfPTable table) {
    table.addCell(clientRow.name);
    table.addCell(clientRow.name);
    table.addCell(clientRow.name);
  }

  @Override
  public void finish(String authorName) {
    document.close();
  }

  private void add(Element element) {
    try {
      document.add(element);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    OutputStream outf = new FileOutputStream(new File("/Users/adilbekmailanov/Desktop/iTextHelloWorld.pdf"));

    ClientRenderImplXlsx asd = new ClientRenderImplXlsx(outf);

    asd.start("fds gsdf", new Date());

    for (int i=0;i<10; i++) {
      ClientRow row = new ClientRow();
      row.id=i+1;
      row.surname = RND.str(10);
      row.name = RND.str(10);
      row.patronymic = RND.str(10);
      row.age = RND.plusInt(60);
      row.middle_balance = RND.plusInt(10000);
      row.max_balance = RND.plusInt(10000);
      row.min_balance = RND.plusInt(10000);
      asd.append(row);
    }

    asd.finish("Adilbek");
  }
}
