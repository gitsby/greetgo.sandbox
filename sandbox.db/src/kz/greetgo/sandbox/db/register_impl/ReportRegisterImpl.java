package kz.greetgo.sandbox.db.register_impl;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.db.client_queries.ClientRecordsRender;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.IOException;
import java.util.Date;

@Bean
public class ReportRegisterImpl implements ReportRegister {

  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public void renderClientList(ClientRecordFilter filter, String userName, ClientRecordsReportView view) throws IOException, DocumentException {
    jdbc.get().execute(new ClientRecordsRender(filter, view));
    view.finish(userName, new Date());
  }
}
