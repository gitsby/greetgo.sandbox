package kz.greetgo.sandbox.db.classes;

import kz.greetgo.sandbox.controller.model.ClientRecordRow;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestView implements ClientRecordsReportView {

  public List<ClientRecordRow> rows;
  public String userName;

  @Override
  public void start() {
    rows = new ArrayList<>();
  }

  @Override
  public void appendRow(ClientRecordRow clientRecord) {
    rows.add(clientRecord);
  }

  @Override
  public void finish(String userName, Date currentDate) {
    this.userName = userName;
  }
}
