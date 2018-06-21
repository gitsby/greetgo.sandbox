package kz.greetgo.sandbox.controller.render.impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.util.RND;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

@Bean
public class ClientRenderImplPdf implements ClientRender {

  private OutputStream out;

  public ClientRenderImplPdf(OutputStream out) {
    this.out = out;
  }

  @Override
  public void start(String name, Date createdDate) {

  }

  @Override
  public void append(ClientRow clientRow) {

  }
  @Override
  public void finish() {
  }

  public static void main(String[] args) throws Exception {
    OutputStream outf = new FileOutputStream(new File("/Users/adilbekmailanov/Desktop/iTextHelloWorld.pdf"));

    ClientRenderImplXlsx asd = new ClientRenderImplXlsx(outf);

    asd.start( RND.str(10), new Date());

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

    asd.finish();
  }
}
