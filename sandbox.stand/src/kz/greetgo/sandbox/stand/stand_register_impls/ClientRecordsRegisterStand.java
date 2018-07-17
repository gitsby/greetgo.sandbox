package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.errors.InvalidClientData;
import kz.greetgo.sandbox.controller.errors.NoCharmError;
import kz.greetgo.sandbox.controller.errors.NoClient;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRecordsRegister;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.stand.beans.StandJsonDb;

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
public class ClientRecordsRegisterStand implements ClientRecordsRegister {

    public BeanGetter<StandJsonDb> db;
    public BeanGetter<AuthRegister> authRegister;
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
    public ClientRecordsToSend getClientRecords(int skipNumber, int limit, String sortDirection, String sortType, String filterType, String filterText){
        //System.out.println(skipNumber+" \n"+
//                           limit+" \n"+
//                            sortDirection+" \n"+
//                            filterType+" \n"+
//                            filterText);

        if(filterText==null){
            filterText="";
        }
        ClientRecordsToSend queriedClientRecords  = new ClientRecordsToSend();
        Filter filter = new Filter();
        filter.filterText=filterText;
        filter.filterType=FilterType.valueOf(filterType.toUpperCase());
        db.get().clientRecordsCreate(filter);

        queriedClientRecords.table=db.get().clientRecordsToSend.table.stream().sorted(((o1, o2) -> {
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
        queriedClientRecords.size=getTableSize();
        return queriedClientRecords;
    }

    public Boolean checkParams(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText){
        return  skipNumber==null || limit==null ||
                sortDirection==null || sortType==null ||
                filterText==null || filterType==null ||
                skipNumber==-1 || limit==-1 ||
                sortDirection.isEmpty() ||sortType.isEmpty() ||
                filterText.isEmpty() || filterType.isEmpty() ||
                !sortDirection.matches("(ASC|DESC)") ||
                !sortType.matches("(FULLNAME|AGE|MAXBALANCE|MINBALANCE|TOTALBALANCE)") ||
                !filterType.matches("NAME|SURNAME|PATRONYMIC");
    }

    public int getTableSize(){
        try {
            return db.get().clientRecordsToSend.table.size();
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
    public Client getClientDetails(Integer clientId){
        //System.out.println(clientId);
        if (clientId==null||!isThereSuchClient(clientId)){
            throw new NoClient();
        }
        return db.get().clients.data.stream().filter((client) -> Objects.equals(clientId, client.id)).findFirst().get();
    }


    @Override
    public Integer createClient(Client client){
        if(!checkForConstraints(client)){
            throw new InvalidClientData();
        }
        client.validity=true;
        //System.out.println("\n\n\n"+client+"\n\n\n");
        if(!isThereSuchCharm(client.charmId)){
            throw new NoCharmError();
        }

        client.id = db.get().lastId+1;
        db.get().clients.data.add(client);
        Account account = new Account();
        account.registeredAt = System.currentTimeMillis();
        account.id = db.get().accounts.data.size();
        account.clientId = client.id;
        account.moneyNumber=0;
        db.get().accounts.data.add(account);
        db.get().updateDB();
        db.get().lastId=db.get().lastId+1;
        return getLastId();
    }

    public Boolean isThereSuchCharm(int id){

        return db.get().charms.data.stream().anyMatch(charm->charm.id.equals(id));
    }


    private Boolean checkForConstraints(Client client){
        if (    client.name==null || client.name.isEmpty() ||
                client.surname==null || client.surname.isEmpty() ||
                client.charmId==null|| client.genderType==null ||
                client.phones==null || client.registeredAddress==null ||
                client.birthDate==null ||
                client.registeredAddress.street == null || client.registeredAddress.street.isEmpty() ||
                client.registeredAddress.flat == null || client.registeredAddress.flat.isEmpty() ||
                client.registeredAddress.house == null || client.registeredAddress.house.isEmpty() ){
            return false;
        }
        boolean va=true;
        boolean mob=false;
        for(Phone phone: client.phones){
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
    public String changeClient(Client client){
        if (!checkForConstraints(client)){
            throw new InvalidClientData();
        }

        if(!isThereSuchCharm(client.charmId)){
            throw new NoCharmError();
        }

        if(client.id==null||!isThereSuchClient(client.id)){
            throw new NoClient();
        }
        client.validity=true;

        db.get().clients.data.removeIf(client1 -> client.id.equals(client1.id));
        db.get().clients.data.add(client);
        db.get().updateDB();
        return "Client was successfully updated";
    }

    private boolean isThereSuchClient(int id) {
        return db.get().clients.data.stream().anyMatch(client -> client.id.equals(id));
    }

    @Override
    public String deleteClient(Integer clientId){

        if(clientId==null||!isThereSuchClient(clientId)){
            throw new NoClient();
        }

        db.get().clients.data.removeIf(client -> clientId.equals(client.id));
        db.get().updateDB();
        return "Client was successfully deleted";
    }

    @Override
    public String makeReport(String sortDirection, String sortType, String filterType,
                             String filterText,String client, String reportType) throws Exception{
        ReportClientRecordsView reportClientRecordsView;
        OutputStream out;
        Date date = new Date();
        client = authRegister.get().getUserInfo(client).accountName;
        String filename =client+"_"+date.getTime();
        if(reportType.equals("PDF")){
            filename+="."+reportType;
            out = new FileOutputStream(new File(reportsPath+filename));
            reportClientRecordsView = new ReportClientRecordsViewPdf(out);
        }else if(reportType.equals("XLSX")){
            filename+="."+reportType;
            out = new FileOutputStream(new File(reportsPath+filename));
            reportClientRecordsView = new ReportClientRecordsViewXlsx(out);
        }else {
            return "-1";
        }

        ClientRecordsToSend clientRecordsToSend;

        reportClientRecordsView.start(client,date);
        getClientRecords(0,0, sortDirection,sortType, filterType, filterText);
        clientRecordsToSend =getClientRecords(0,getTableSize(), sortDirection,sortType, filterType, filterText);
        int j=1;
        for (ClientRecord clientRecord : clientRecordsToSend.table) {
            //System.out.println(j+" "+ clientRecord.toString());
            reportClientRecordsView.append(clientRecord,j);
            j++;
        }

        reportClientRecordsView.finish();
        return filename;
    }

    @Override
    public void downloadReport(String filename, BinResponse response)
            throws Exception{
        if(!(new File(reportsPath+filename)).exists()){
            return;
        }
        String urlEncodedFileName = URLEncoder.encode(filename, "UTF-8");
        response.setContentType("application/octet-stream");
        response.setFilename(urlEncodedFileName);
        OutputStream outputStream =response.out();
        FileInputStream fileInputStream = (new FileInputStream(new File(reportsPath + filename)));
        byte[] buffer = new byte[4096];
        int len = 0;
        while((len=fileInputStream.read(buffer))>=0){
            outputStream.write(buffer,0,len);
        }
        fileInputStream.close();
        response.flushBuffers();
    }

    @Override
    public Charms getCharms(){

        return db.get().charms;
    }

    @Override
    public void reportTest(ClientRecord clientRecord, int index, ReportClientRecordsView view){
        return;
    }


}
