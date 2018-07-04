package kz.greetgo.sandbox.controller.render.impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Bean
public class ClientRenderImplXlsx implements ClientRender {

  private final OutputStream out;
  private Xlsx xlsx;
  private Sheet sheet;

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
  public void append(ClientRecord clientRecord) {
    sheet.row().start();
    sheet.cellInt(1, clientRecord.id);
    sheet.cellStr(2, clientRecord.surname);
    sheet.cellStr(3, clientRecord.name);
    sheet.cellStr(4, clientRecord.patronymic);
    sheet.cellDouble(5, clientRecord.middle_balance);
    sheet.cellDouble(6, clientRecord.max_balance);
    sheet.cellDouble(7, clientRecord.min_balance);
    sheet.row().finish();
  }

  @Override
  public void finish() {
    sheet.skipRow();
    xlsx.complete(out);
    try {
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    OutputStream outf = new FileOutputStream(new File("/Users/adilbekmailanov/Desktop/new.xlsx"));

    ClientRenderImplXlsx asd = new ClientRenderImplXlsx(outf);

    asd.start(RND.str(10), new Date());

    for (int i=0;i<10; i++) {
      ClientRecord row = new ClientRecord();
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

    asd.finish();
  }
}
