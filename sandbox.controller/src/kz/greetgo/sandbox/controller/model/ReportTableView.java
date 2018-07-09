package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public interface ReportTableView {

    void start(String user, Date reportDate);

    void append(TableModel tableModel,int index);

    void finish();

}
