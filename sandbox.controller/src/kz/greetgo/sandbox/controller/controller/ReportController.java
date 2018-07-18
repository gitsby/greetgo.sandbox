package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.controller.report.ClientRecordsViewPdf;
import kz.greetgo.sandbox.controller.report.ClientRecordsViewXlsx;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.IOException;

@Bean
@Mapping("/report")
public class ReportController implements Controller {

  public BeanGetter<ReportRegister> clientRegister;

  @MethodFilter(RequestMethod.GET)
  @Mapping("/render")
  public void render(
    @Json @Par("filter") ClientRecordFilter filter,
    @Par("fileName") String fileName,
    @Par("fileType") String fileType,
    @Par("accName") String accName,
    BinResponse binResponse) throws Exception {

    binResponse.setFilename(String.format("%s." + fileType, fileName));
    binResponse.setContentTypeByFilenameExtension();

    ClientRecordsReportView view;

    binResponse.out().flush();

    if (fileType.equals("xlsx")) {
      view = new ClientRecordsViewXlsx(binResponse.out());
    } else {
      view = new ClientRecordsViewPdf(binResponse.out());
    }
    clientRegister.get().renderClientList(filter, accName, view);
    binResponse.flushBuffers();
  }
}
