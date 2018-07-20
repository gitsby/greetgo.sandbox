package kz.greetgo.sandbox.stand.client_records_report;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.Date;

public interface ClientRecordsReportView {

  void start() throws DocumentException;

  void appendRow(ClientRecordRow clientRecord);

  void finish(String userName, Date currentDate) throws DocumentException, IOException;
}
