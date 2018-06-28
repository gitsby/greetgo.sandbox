package kz.greetgo.sandbox.stand.client_records_report;

import java.util.Date;

public interface ClientRecordsReportView {

  void start();

  void appendRow(ClientRecordRow clientRecord);

  void finish(String userName, Date currentDate);
}
