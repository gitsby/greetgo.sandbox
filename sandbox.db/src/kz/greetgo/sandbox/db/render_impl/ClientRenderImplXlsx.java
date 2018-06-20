package kz.greetgo.sandbox.db.render_impl;

import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClientRenderImplXlsx implements ClientRender {

  private final OutputStream out;
  private Xlsx xlsx;
  private Sheet sheet;

  private int rowNo = 0;

  public ClientRenderImplXlsx(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String name, Date createdDate) {
    xlsx = new Xlsx();

    sheet = xlsx.newSheet(true);

    sheet.row().start();
    sheet.cellStr(1, name);
    sheet.cellStr(2, "от " + (new SimpleDateFormat("dd/MM/yyyy").format(createdDate)));
    sheet.row().finish();

    sheet.skipRow();

    sheet.row().start();
    sheet.cellStr(1, "ID");
    sheet.cellStr(2, "SURNAME");
    sheet.cellStr(3, "NAME");
    sheet.cellStr(4, "PATRONYMIC");
    sheet.cellStr(5, "AGE");
    sheet.cellStr(6, "MIDDLE BALANCE");
    sheet.cellStr(7, "MAX BALANCE");
    sheet.cellStr(8, "MIN BALANCE");
    sheet.row().finish();
  }

  @Override
  public void append(ClientRow clientRow) {
    sheet.row().start();
    sheet.cellInt(1, clientRow.id);
    sheet.cellStr(2, clientRow.surname);
    sheet.cellStr(3, clientRow.name);
    sheet.cellStr(4, clientRow.patronymic);
    sheet.cellDouble(5, clientRow.middle_balance);
    sheet.cellDouble(6, clientRow.max_balance);
    sheet.cellDouble(7, clientRow.min_balance);
    sheet.row().finish();
  }

  @Override
  public void finish(String userName) {
    sheet.skipRow();

    sheet.row().start();
    sheet.cellStr(1, "Сформирован: " + userName);
    sheet.row().finish();

    xlsx.complete(out);
  }

  private Integer getRowNo() {
    return rowNo++;
  }

  public static void main(String[] args) throws Exception {
    OutputStream outf = new FileOutputStream(new File("/Users/adilbekmailanov/Desktop/new.xlsx"));

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
