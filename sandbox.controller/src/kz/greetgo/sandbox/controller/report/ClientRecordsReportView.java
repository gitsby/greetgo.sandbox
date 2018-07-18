package kz.greetgo.sandbox.controller.report;

import com.itextpdf.text.DocumentException;
import kz.greetgo.sandbox.controller.model.ClientRecordRow;

import java.io.IOException;
import java.util.Date;

public interface ClientRecordsReportView {

  void start();

  void appendRow(ClientRecordRow clientRecord);

  void finish(String userName, Date currentDate) throws IOException, DocumentException;
}
