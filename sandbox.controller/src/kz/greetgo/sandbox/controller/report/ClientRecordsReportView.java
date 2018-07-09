package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecordRow;

import java.util.Date;

public interface ClientRecordsReportView {

  void start();

  void appendRow(ClientRecordRow clientRecord);

  void finish(String userName, Date currentDate);
}
