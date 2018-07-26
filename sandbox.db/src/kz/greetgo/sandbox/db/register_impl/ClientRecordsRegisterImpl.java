package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.errors.InvalidClientData;
import kz.greetgo.sandbox.controller.errors.InvalidParams;
import kz.greetgo.sandbox.controller.errors.NoCharmError;
import kz.greetgo.sandbox.controller.errors.NoClient;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.DbCharm;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClient;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientAddress;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientPhone;
import kz.greetgo.sandbox.controller.register.ClientRecordsRegister;
import kz.greetgo.sandbox.db.dao.ClientRecordsDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.NoSuchElementException;

// TODO: war не собирается, исправь ошибку.
// TODO: убери папки /front & /myFront. Не храни в проекте ничего лишнего
// DONE
@Bean
public class ClientRecordsRegisterImpl implements ClientRecordsRegister {

  public BeanGetter<ClientRecordsDao> clientRecordsDao;

  public DbModelConverter dbModelConverter = new DbModelConverter();

  // TODO: relative path!
  private String reportsPath = "D:/greetgonstuff/greetgo.sandbox/reports/";

  @Override
  public ClientRecordsToSend getClientRecords(int skipNumber, int limit, String sortDirection, String sortType, String filterType, String filterText){

    if(filterText==null){
      filterText="";
    }
    ClientRecordsToSend clientRecordsToSend = new ClientRecordsToSend();

    sortDirection = sortDirection.toUpperCase();
    filterType = filterType.toUpperCase();
    sortType = sortType.toUpperCase();
    filterText = "%"+ filterText + "%";
    if (checkParams(skipNumber,limit,sortDirection,sortType,filterType,filterText)){
      throw new InvalidParams();
    }

    if(sortType.equals("FULLNAME")){
      clientRecordsToSend.table= sortDirection.equals("DESC")?
        clientRecordsDao.get().getFullNameDesc(skipNumber, limit, filterType, filterText):
        clientRecordsDao.get().getFullNameAsc(skipNumber, limit, filterType, filterText);
    }
    if(sortType.equals("AGE")){
      clientRecordsToSend.table= sortDirection.equals("DESC")?
        clientRecordsDao.get().getAgeDesc(skipNumber, limit, filterType, filterText):
        clientRecordsDao.get().getAgeAsc(skipNumber, limit, filterType, filterText);
    }
    if(sortType.equals("MINBALANCE")){
      clientRecordsToSend.table= sortDirection.equals("DESC")?
        clientRecordsDao.get().getMinBalanceDesc(skipNumber, limit, filterType, filterText):
        clientRecordsDao.get().getMinBalanceAsc(skipNumber, limit, filterType, filterText);
    }
    if(sortType.equals("MAXBALANCE")){
      clientRecordsToSend.table= sortDirection.equals("DESC")?
        clientRecordsDao.get().getMaxBalanceDesc(skipNumber, limit, filterType, filterText):
        clientRecordsDao.get().getMaxBalanceAsc(skipNumber, limit, filterType, filterText);
    }
    if(sortType.equals("TOTALBALANCE")){
      clientRecordsToSend.table= sortDirection.equals("DESC")?
        clientRecordsDao.get().getTotalBalanceDesc(skipNumber, limit, filterType, filterText):
        clientRecordsDao.get().getTotalBalanceAsc(skipNumber, limit, filterType, filterText);
    }

    clientRecordsToSend.size=clientRecordsDao.get().getClientRecordsSize(filterType,filterText);
    return clientRecordsToSend;
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


  @Override
  public Client getClientDetails(Integer clientId){

    if(clientId==null||isThereSuchClient(clientId)){
      throw new NoClient();
    }

    DbClient dbClient = clientRecordsDao.get().getClientDetails(clientId);
    DbCharm  dbCharm = clientRecordsDao.get().getCharm(1);

    DbClientPhone[] dbClientPhones = clientRecordsDao.get().getPhones(clientId);

    DbClientAddress dbClientAddressFactual = clientRecordsDao.get().getClientAddress(clientId, AddressType.FACT.toString());

    DbClientAddress dbClientAddressRegistered = clientRecordsDao.get().getClientAddress(clientId, AddressType.REG.toString());

    Client client = dbModelConverter.convertToClient(dbClient,dbClientPhones,dbClientAddressFactual,dbClientAddressRegistered);
    return client;
  }

  private Boolean checkForContraints(Client client){
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
  public Integer createClient(Client client){

    if(!checkForContraints(client))
      throw new InvalidClientData();

    if(!isThereSuchCharm(client.charmId)){
      throw new NoSuchElementException();
    }

    client.validity=true;

    DbClient dbClient = dbModelConverter.convertToDbClient(client);

    clientRecordsDao.get().insertClient(dbClient);
    client.id = clientRecordsDao.get().getLastClientId();

    DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(client);

    for (DbClientPhone dbClientPhone: dbClientPhones) {
      clientRecordsDao.get().insertPhone(dbClientPhone);
    }
    clientRecordsDao.get().insertAddress(dbModelConverter.convertToDbClientAddressRegistered(client));
    clientRecordsDao.get().insertAddress(dbModelConverter.convertToDbClientAddressFactual(client));

    clientRecordsDao.get().insertAccount(dbModelConverter.convertToDbClientAccount(client));
    clientRecordsDao.get().insertAccount(dbModelConverter.convertToDbClientAccount(client));
    return client.id;
  }


  @Override
  public String changeClient(Client client){


    if (client.id==null||!checkForContraints(client)){
      throw new InvalidClientData();
    }

    if(!isThereSuchCharm(client.charmId)){
      throw new NoCharmError();
    }

    if(!isThereSuchClient(client.id)){
      throw new NoClient();
    }

    client.validity=true;

    DbClientPhone[] dbClientPhonesLoaded = clientRecordsDao.get().getPhones(client.id);
    DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(client);

    for (int i = 0; i <dbClientPhones.length; i++) {
      for (int j = 0; j <dbClientPhonesLoaded.length ; j++) {
        if(dbClientPhones[i].number.equals(dbClientPhonesLoaded[j].number) && dbClientPhones[i].validity!=dbClientPhonesLoaded[i].validity){
          clientRecordsDao.get().updatePhone(dbClientPhones[i]);
          dbClientPhones[i]=new DbClientPhone();
          dbClientPhones[i].number="0";
        }

      }
    }
    for (DbClientPhone dbClientPhone : dbClientPhones) {
      if (!dbClientPhone.number.equals("0"))
        clientRecordsDao.get().insertPhone(dbClientPhone);
    }


    clientRecordsDao.get().updateClient(dbModelConverter.convertToDbClient(client));
    clientRecordsDao.get().updateAddress(dbModelConverter.convertToDbClientAddressRegistered(client));
    clientRecordsDao.get().updateAddress(dbModelConverter.convertToDbClientAddressFactual(client));

    return "1";
  }

  @Override
  public String deleteClient(Integer clientId){
    if(!isThereSuchClient(clientId)){
      throw new NoClient();
    }
    clientRecordsDao.get().deleteClient(clientId);
    clientRecordsDao.get().deletePhone(clientId);
    clientRecordsDao.get().deleteAccount(clientId);
    return "1";
  }

  int getClientRecordsSize(String filterType, String filterText){
    int size = 0;
      size=clientRecordsDao.get().getClientRecordsSize(filterType,filterText);
    return size;
  }

  // TODO: протестировать генерацию отчетов на 100 000, 1М, 10М запиясях
  @Override
  public String makeReport(String sortDirection, String sortType, String filterType,
                           String filterText,String client, String reportType) throws Exception{

      int size = getClientRecordsSize(filterType,filterText);
      ReportClientRecordsView reportClientRecordsView;
      OutputStream out;
      Date date = new Date();
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

        // TODO: неверное уведомление об ошибки!
        return "-1";
      }
      ClientRecordsToSend clientRecordsToSend;

      reportClientRecordsView.start(client,date);
      for (int i = 0; i < size-size%4; i=i+4) {
        clientRecordsToSend =getClientRecords(i,4, sortDirection,sortType, filterType, filterText);
        int j=0;
        for (ClientRecord clientRecord : clientRecordsToSend.table) {
          reportClientRecordsView.append(clientRecord,i+j);
          j++;
        }
      }
      clientRecordsToSend =getClientRecords(size-size%4,4,sortDirection,sortType,filterType,filterText);
      int j=0;
      for (ClientRecord clientRecord : clientRecordsToSend.table) {
        reportClientRecordsView.append(clientRecord,j+size);
        j++;
    }

    reportClientRecordsView.finish();
    return filename;
  }

  @Override
  public void downloadReport(String filename, BinResponse response) throws Exception{
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
    public void reportTest(ClientRecord clientRecord, int i, ReportClientRecordsView view) throws Exception {
      view.start("client", (new Date()));
      view.append(clientRecord, i);
      view.finish();
    }

    @Override
  public Charms getCharms(){
      return clientRecordsDao.get().getCharms();
  }
  public Boolean isThereSuchCharm(int id){ return clientRecordsDao.get().isThereSuchCharm(id) > 0; }
  public Boolean isThereSuchClient(int id){ return clientRecordsDao.get().countClientsWithClientId(id)>0;}
}



