package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.stand.beans.StandJsonDb;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class TableRegisterStand implements TableRegister {

    public BeanGetter<StandJsonDb> db;

    public String reportsPath="D:/greetgonstuff/greetgo.sandbox/reports/";

    public enum SortType{
        FULLNAME,
        CHARM,
        AGE,
        TOTALBALANCE,
        MAXBALANCE,
        MINBALANCE,
    };



    @Override
    public TableToSend getTableData(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText){
        System.out.println(skipNumber+" \n"+
                           limit+" \n"+
                            sortDirection+" \n"+
                            filterType+" \n"+
                            filterText);
        if(skipNumber==null){
            skipNumber=0;
        }
        if(filterText==null){
            filterText="";
        }
        TableToSend queriedTable  = new TableToSend();
        db.get().filter.filterText=filterText;
        db.get().filter.filterType=FilterType.valueOf(filterType.toUpperCase());
        db.get().tableCreate();

        queriedTable.table=db.get().table.table.stream().sorted(((o1, o2) -> {
            SortType enumSortType = SortType.valueOf(sortType.toUpperCase());
            switch (enumSortType) {
                case FULLNAME:
                    return "DESC".equals(sortDirection.toUpperCase())?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
                case CHARM:
                    return "DESC".equals(sortDirection.toUpperCase())?-o1.charm.compareTo(o2.charm):o1.charm.compareTo(o2.charm);
                case AGE:
                    return "DESC".equals(sortDirection.toUpperCase())?-Long.compare(o1.age,o2.age):Long.compare(o1.age,o2.age);
                case TOTALBALANCE:
                    return "DESC".equals(sortDirection.toUpperCase())?-Double.compare(o1.totalBalance,o2.totalBalance):Double.compare(o1.totalBalance,o2.totalBalance);
                case MAXBALANCE:
                    return "DESC".equals(sortDirection.toUpperCase())?-Double.compare(o1.maxBalance,o2.maxBalance):Double.compare(o1.maxBalance,o2.maxBalance);
                case MINBALANCE:
                    return "DESC".equals(sortDirection.toUpperCase())?-Double.compare(o1.minBalance,o2.minBalance):Double.compare(o1.minBalance,o2.minBalance);
                default:
                    return "DESC".equals(sortDirection.toUpperCase())?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
            }
        })).skip(skipNumber).limit(limit).collect(Collectors.toCollection(ArrayList::new));
        queriedTable.size=getTableSize();
        return queriedTable;
    }

    public int getTableSize(){
        try {
            return db.get().table.table.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getLastId(){
        try {
            return db.get().lastId;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public User getExactUser(Integer userID){
        try {
            return db.get().users.data.stream().filter((user) -> Objects.equals(userID, user.id)).findFirst().get();
        } catch (Exception e){
            e.printStackTrace();
            return new User();
        }
    }


    @Override
    public Integer createUser(User user){
        if(!checkForValidity(user)){
            return -1;
        }
        user.id = db.get().lastId+1;
        db.get().users.data.add(user);
        Account account = new Account();
        account.registeredAt = System.currentTimeMillis();
        account.id = db.get().accounts.data.size();
        account.userID = user.id;
        account.moneyNumber=0;
        db.get().accounts.data.add(account);
        db.get().updateDB();
        return getLastId();
    }


    @Override
    public String[] getCharms(){
        return null;
    }

    private Boolean checkForValidity(User user){
        if (    user.name==null || user.name.isEmpty() ||
                user.surname==null || user.surname.isEmpty() ||
                user.charm==null || user.genderType==null ||
                user.phones==null || user.registeredAddress==null ||
                user.birthDate==null ||
                user.registeredAddress.street == null || user.registeredAddress.street.isEmpty() ||
                user.registeredAddress.flat == null || user.registeredAddress.flat.isEmpty() ||
                user.registeredAddress.house == null || user.registeredAddress.house.isEmpty() ){
            return false;
        }
        boolean va=true;
        boolean mob=false;
        for(Phone phone: user.phones){
            if(phone.number.matches("^(\\d{11})?$")){

                va=va&&true;
                if(phone.phoneType==PhoneType.MOBILE ) {
                    mob=true;
                }
            }else {
                va=false;
            }
        }
        return va&&mob;
    }

    @Override
    public String changeUser(User user){
        if (!checkForValidity(user)){
            return "User is not valid!";
        }
        db.get().users.data.removeIf(user1 -> user.id.equals(user1.id));
        db.get().users.data.add(user);
        db.get().updateDB();
        return "User was successfully updated";
    }

    @Override
    public String deleteUser(Integer userID){
        db.get().users.data.removeIf(user -> userID.equals(user.id));
        db.get().updateDB();
        return "User was successfully deleted";
    }

    @Override
    public String makeReport(String sortDirection, String sortType, String filterType,
                             String filterText,String user, String reportType) throws Exception{
        ReportTableView reportTableView;
        OutputStream out;
        Date date = new Date();
        String filename =user+"_"+date.getTime();
        if(reportType.equals("PDF")){
            filename+="."+reportType;
            out = new FileOutputStream(new File(reportsPath+filename));
            reportTableView = new ReportTableViewPdf(out);
        }else if(reportType.equals("XML")){
            filename+="."+reportType;
            out = new FileOutputStream(new File(reportsPath+filename));
            reportTableView = new ReportTableViewXlsx(out);
        }else {
            return "-1";
        }
        TableToSend tableToSend;

        reportTableView.start(user,date);
        for (int i = 0; i < getTableSize()-getTableSize()%4; i=i+4) {
            tableToSend=getTableData(i,4, sortDirection,sortType, filterType, filterText);
            int j=0;
            for (TableModel tableModel:tableToSend.table) {
                reportTableView.append(tableModel,i+j);
                j++;
            }
        }
        tableToSend=getTableData(getTableSize()-getTableSize()%4,4,sortDirection,sortType,filterType,filterText);
        int j=0;
        for (TableModel tableModel:tableToSend.table) {
            reportTableView.append(tableModel,j+getTableSize());
            j++;
        }

        reportTableView.finish();
        return filename;
    }

    public void downloadReport(String filename, HttpServletResponse response)
            throws Exception{
        if(!(new File(reportsPath+filename)).exists()){
            return;
        }
        String urlEncodedFileName = URLEncoder.encode(filename, "UTF-8");
        response.setHeader("Content-Disposition","attachment; filename="+urlEncodedFileName);
        ServletOutputStream servletOutputStream= response.getOutputStream();
        FileInputStream fileInputStream = (new FileInputStream(new File(reportsPath + filename)));
        byte[] buffer = new byte[8];
        int len = 0;
        while((len=fileInputStream.read(buffer))>=0){
            servletOutputStream.write(buffer,0,len);
        }
        fileInputStream.close();
        response.flushBuffer();
    }


}
