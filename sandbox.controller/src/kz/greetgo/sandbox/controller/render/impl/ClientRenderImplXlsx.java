package kz.greetgo.sandbox.controller.render.impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.msoffice.xlsx.gen.Sheet;
import kz.greetgo.msoffice.xlsx.gen.Xlsx;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.render.ClientRender;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Bean//FIXME это не бин
public class ClientRenderImplXlsx implements ClientRender {

  private static Logger logger = Logger.getLogger(ClientRenderImplXlsx.class);

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
      logger.error(e);
    }
  }
}
