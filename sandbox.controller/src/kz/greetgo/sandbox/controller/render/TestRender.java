package kz.greetgo.sandbox.controller.render;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.render.impl.ClientRenderImplPdf;
import kz.greetgo.sandbox.controller.render.impl.ClientRenderImplXlsx;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestRender {

  public static void main(String[] args) throws FileNotFoundException {
    createTestFile(new ClientRenderImplPdf(getOutputStream(getFileName("TEST", "pdf"))));
    createTestFile(new ClientRenderImplXlsx(getOutputStream(getFileName("TEST", "xlsx"))));
  }

  private static OutputStream getOutputStream(String fileName) throws FileNotFoundException {
    return new FileOutputStream(new File(System.getProperty("user.home")+"/Desktop/"+fileName));
  }

  private static String getFileName(String fileName, String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_dd_MM_HH_SS_MM");
    return String.format("%s_%s.%s", fileName, dateFormat.format(new Date()), format);
  }

  private static void createTestFile(ClientRender render) {
    render.start(RND.str(10), new Date());
    for (int i=0;i<30; i++) render.append(getRandomClientRecord(i));
    render.finish();
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
