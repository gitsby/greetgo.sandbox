package kz.greetgo.sandbox.controller.model;

import java.io.OutputStream;
import java.util.Date;

public class ReportTableViewPdf implements ReportTableView {

    private final OutputStream out;

    public ReportTableViewPdf(OutputStream out){
        this.out=out;
    }

    @Override
    public void start(String user, Date reportDate){

    }

    public void append(TableModel tableModel,int index){

    }

    public void finish(){

    }

}
