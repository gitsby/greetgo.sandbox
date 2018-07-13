package kz.greetgo.sandbox.controller.model;

import java.util.Date;

public interface ReportClientRecordsView {

    void start(String user, Date reportDate)throws Exception;

    void append(ClientRecord clientRecord, int index)throws Exception;

    void finish()throws Exception;

}
