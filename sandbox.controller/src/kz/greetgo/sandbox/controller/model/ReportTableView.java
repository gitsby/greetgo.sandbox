package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public interface ReportTableView {

    void start(String user, Date reportDate)throws Exception;

    void append(TableModel tableModel,int index)throws Exception;

    void finish()throws Exception;

}
