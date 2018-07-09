package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;

public interface ReportRegister {

  void renderClientList(ClientRecordFilter filter, String userName, ClientRecordsReportView view) throws Exception;

}
