package kz.greetgo.sandbox.db.register_impl;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.*;
import kz.greetgo.sandbox.controller.register.ClientRecordsRegister;
import kz.greetgo.sandbox.db.dao.ClientRecordsDao;
import kz.greetgo.sandbox.db.test.dao.ClientRecordsTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class ClientRecordsRegisterImplTest extends ParentTestNg{
    public BeanGetter<ClientRecordsRegister> tableRegister;
    public BeanGetter<ClientRecordsTestDao> tableTestDao;
    public BeanGetter<ClientRecordsDao> tableDao;

    TestDataGenerator testDataGenerator = new TestDataGenerator();
    DbModelConverter dbModelConverter = new DbModelConverter();

    @Test
    public void insertClientX10Test(){
        deleteAllData();
        Client[] sentClients = new Client[10];
        Client[] gotClients = new Client[10];

        for(int i=0; i<10; i++) {
            sentClients[i]=testDataGenerator.generateClient();
            int id = tableRegister.get().createClient(sentClients[i]);
            sentClients[i].id=id;
            gotClients[i]=tableRegister.get().getClientDetails(id);
        }

        for(int i=0; i<10; i++){
            assertThat(sentClients[i].equals(gotClients[i]));
        }
    }

    @Test
    public void insertClientsWithNullParsTest(){
        deleteAllData();
        String[] move  = {"name","surname","gender","charm","registeredAddress","birthDate"};
        for(String option: move) {
            Client client = testDataGenerator.generateClient();
            if(option.equals("name")){
                client.name=null;
            } else if(option.equals("surname")){
                client.surname=null;
            } else if(option.equals("gender")){
                client.genderType = null;
            } else if(option.equals("charm")){
                client.charmId = null;
            } else if(option.equals("registeredAddress")){
                client.registeredAddress = null;
            } else if(option.equals("birthDate")){
                client.birthDate = null;
            }
            Integer id = tableRegister.get().createClient(client);
            assertThat(id==-1);
        }
        for(String option: move) {
            Client client =testDataGenerator.generateClient();
            if(option.equals("name")){
                client.name="";
            } else if(option.equals("surname")){
                client.surname="";
            } else if(option.equals("gender")){
                client.genderType = null;
            } else if(option.equals("charm")){
                client.charmId = 0;
            } else if(option.equals("registeredAddress")){
                client.registeredAddress.house = "";
                client.registeredAddress.flat = "";
                client.registeredAddress.street = "";
            } else if(option.equals("birthDate")){
                client.birthDate = null;
            }
            Integer id = tableRegister.get().createClient(client);
            assertThat(id==-1);
        }

    String[] moveByType = {"NOMOBILE", "LESSTHAN11", "LETTERS"};

    for (String option : moveByType) {
      Client client = testDataGenerator.generateClient();
      if (option.equals("NOMOBILE")) {
        for (int j = 0; j < client.phones.length; j++) {
          if (client.phones[j].phoneType == PhoneType.MOBILE) {
            client.phones[j].phoneType = PhoneType.WORK;
          }
        }
      }
      if (option.equals("LESSTHAN11")) {
        client.phones[0].number = "123456";
      }
      if (option.equals("LETTERS")) {
        client.phones[0].number = "asdsada";
      }
      Integer id = tableRegister.get().createClient(client);
      assertThat(id).isEqualTo(-1);

    }

    @Test
    public void getClientDetailsTest(){
        deleteAllData();
      
        DbCharm dbCharm = testDataGenerator.generateRandomCharm(RND.str(5));
        tableTestDao.get().insertCharm(dbCharm);
        dbCharm.id = tableTestDao.get().getCharmId(dbCharm.name);

        DbClient dbClient = dbModelConverter.convertToDbClient(client);
        tableTestDao.get().insertClient(dbClient);
        Integer clientId=tableTestDao.get().getLastClientId();
        client.id=clientId;
        DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(client);
        DbClientAddress dbClientAddressFactual = dbModelConverter.convertToDbClientAddressFactual(client);
        DbClientAddress dbClientAddressRegistered = dbModelConverter.convertToDbClientAddressRegistered(client);
        tableTestDao.get().insertAddress(dbClientAddressFactual);
        tableTestDao.get().insertAddress(dbClientAddressRegistered);
        for (DbClientPhone dbClientPhone: dbClientPhones) {
            tableTestDao.get().insertPhone(dbClientPhone);
        }
        Client gotClient =tableRegister.get().getClientDetails(clientId);
        assertThat(client.equals(gotClient));
    }

    @Test
    void getNullClientTest() {
        deleteAllData();
        Client gotClient =tableRegister.get().getClientDetails(0);
        Client client = new Client();
        assertThat(client.equals(gotClient));
    }

    DbCharm dbCharm = testDataGenerator.generateRandomCharm(client.charm);
    tableTestDao.get().insertCharm(dbCharm);
    dbCharm.id = tableTestDao.get().getCharmId(dbCharm.name);

    @Test
    void insertClientTest() {
        deleteAllData();
        Client client = testDataGenerator.generateClient();
        client.id = tableRegister.get().createClient(client);
        Client gotClient = tableRegister.get().getClientDetails(client.id);
        assertThat(client.equals(gotClient));
    }

    @Test
    public void updateClientTest() throws Exception {
        deleteAllData();
        Client client = testDataGenerator.generateClient();
        int clientId = tableRegister.get().createClient(client);
        client = testDataGenerator.generateClient();
        client.id = clientId;
        tableRegister.get().changeClient(client);

        Client gotClient = tableRegister.get().getClientDetails(clientId);
        assertThat(gotClient.equals(client));
    }

    @Test
    public void deleteClientTest() {
        deleteAllData();
        Client client = testDataGenerator.generateClient();
        int clientId = tableRegister.get().createClient(client);
        tableRegister.get().deleteClient(clientId);
        client = tableRegister.get().getClientDetails(clientId);
        assertThat(client.id==-1);

    }
    for (int i = 0; i < 100; i++) {
      Client client = testDataGenerator.generateClient();
      client.name = names.data[i].name;
      client.surname = names.data[i].surname;
      client.patronymic = names.data[i].patronymic;
      clientids[i] = tableRegister.get().createClient(client);
      Integer[] ids = tableDao.get().getAccount(clientids[i]);
      accountids[i * 2] = ids[0];
      accountids[i * 2 + 1] = ids[1];

      DbClientAccount dbClientAccount = new DbClientAccount();
      dbClientAccount.money = mins[i] + 0.0f;
      dbClientAccount.client = clientids[i];
      dbClientAccount.id = accountids[i * 2];
      tableDao.get().updateAccount(dbClientAccount);
      dbClientAccount.money = maxs[i] + 0.0f;
      dbClientAccount.client = clientids[i];
      dbClientAccount.id = accountids[i * 2 + 1];
      tableDao.get().updateAccount(dbClientAccount);
    }

  }

  public enum SortType {
    FULLNAME,
    CHARM,
    AGE,
    TOTALBALANCE,
    MAXBALANCE,
    MINBALANCE,
  }

  ;


  @Test
  void tableFunctionalityTest() {
    deleteAllData();
    enterTheData();
    ClientRecordsToSend myTable = tableRegister.get().getClientRecords(0, 100, "ASC", "FULLNAME", "NAME", "");

    String[] sortDirections = {"ASC", "DESC"};
    String[] sortTypes = {"FULLNAME", "AGE", "TOTALBALANCE", "MINBALANCE", "MAXBALANCE"};
    int[] ranges = {0, 33, 66, 100};

    String[] filterNames = {"adam", "denis", "isaak"};
    String[] filterPatronymics = {"Vladimirovich", "Timofeyevich", "Victorovich"};
    String[] filterSurnames = {"TS([\\w]*?)V", "SIY([\\w]*?)V", "CH([\\w]*?)V"};


    for (String sortDirection : sortDirections) {
      for (String sortType : sortTypes) {
        for (int i = 0; i < ranges.length - 1; i++) {
          ClientRecordsToSend gotTable = tableRegister.get().getClientRecords(ranges[i], ranges[i + 1], sortDirection, sortType, "NAME", "");
          ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
          queriedLocally.table = myTable.table.stream().sorted(((o1, o2) -> {
            SortType enumSortType = SortType.valueOf(sortType.toUpperCase());
            switch (enumSortType) {
              case FULLNAME:
                return "DESC".equals(sortDirection) ? -o1.fullName.compareTo(o2.fullName) : o1.fullName.compareTo(o2.fullName);
              case CHARM:
                return "DESC".equals(sortDirection) ? -o1.charm.compareTo(o2.charm) : o1.charm.compareTo(o2.charm);
              case AGE:
                return "DESC".equals(sortDirection) ? -Long.compare(o1.age, o2.age) : Long.compare(o1.age, o2.age);
              case TOTALBALANCE:
                return "DESC".equals(sortDirection) ? -Double.compare(o1.totalBalance, o2.totalBalance) : Double.compare(o1.totalBalance, o2.totalBalance);
              case MAXBALANCE:
                return "DESC".equals(sortDirection) ? -Double.compare(o1.maxBalance, o2.maxBalance) : Double.compare(o1.maxBalance, o2.maxBalance);
              case MINBALANCE:
                return "DESC".equals(sortDirection) ? -Double.compare(o1.minBalance, o2.minBalance) : Double.compare(o1.minBalance, o2.minBalance);
              default:
                return "DESC".equals(sortDirection) ? -o1.fullName.compareTo(o2.fullName) : o1.fullName.compareTo(o2.fullName);
            }
          })).skip(ranges[i]).limit(ranges[i + 1]).collect(Collectors.toCollection(ArrayList::new));

    @Test
    public void updateNullClient() throws Exception {
        deleteAllData();
        int clientId = 1;
        Client client = testDataGenerator.generateClient();
        client.id=clientId;
        tableRegister.get().changeClient(client);
        Client updatedClient = tableRegister.get().getClientDetails(clientId);
        assertThat(updatedClient.id==-1);
    }
          }
        }
      }
    }


    for (String filterName : filterNames) {
      ClientRecordsToSend gotTable = tableRegister.get()
        .getClientRecords(0, 100, "ASC", "FULLNAME", "NAME", filterName);
      ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
      queriedLocally.table = myTable.table.stream()
        .filter(o1 -> filterName
          .toUpperCase().equals(o1.fullName.split(" ")[1].toUpperCase()))
        .sorted((o1, o2) -> o1.fullName.compareTo(o2.fullName))
        .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));


      assertEquals(queriedLocally.table.size(), gotTable.table.size());
      for (int i = 0; i < queriedLocally.size; i++) {
        assertEquals(queriedLocally.table.get(i).fullName, gotTable.table.get(i).fullName);
      }
    }

    public enum SortType{
        FULLNAME,
        CHARM,
        AGE,
        TOTALBALANCE,
        MAXBALANCE,
        MINBALANCE,
    };






    @Test
    void tableFunctionalityTest(){
        deleteAllData();
        enterTheData();
        ClientRecordsToSend myTable = tableRegister.get().getClientRecords(0,100,"ASC","FULLNAME","NAME","");

        String[] sortDirections = {"ASC","DESC"};
        String[] sortTypes = {"FULLNAME","AGE","TOTALBALANCE","MINBALANCE","MAXBALANCE"};
        int[] ranges = {0,33,66,100};

        String[] filterNames = {"adam","denis","isaak"};
        String[] filterPatronymics = {"Vladimirovich","Timofeyevich","Victorovich"};
        String[] filterSurnames = {"TS([\\w]*?)V","SIY([\\w]*?)V","CH([\\w]*?)V"};


        for(String sortDirection: sortDirections){
            for(String sortType: sortTypes){
                for(int i=0; i<ranges.length-1; i++){
                    ClientRecordsToSend gotTable = tableRegister.get().getClientRecords(ranges[i],ranges[i+1],sortDirection, sortType,"NAME","");
                    ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
                    queriedLocally.table=myTable.table.stream().sorted(((o1, o2) -> {
                        SortType enumSortType = SortType.valueOf(sortType.toUpperCase());
                        switch (enumSortType) {
                            case FULLNAME:
                                return "DESC".equals(sortDirection)?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
                            case AGE:
                                return "DESC".equals(sortDirection)?-Long.compare(o1.age,o2.age):Long.compare(o1.age,o2.age);
                            case TOTALBALANCE:
                                return "DESC".equals(sortDirection)?-Double.compare(o1.totalBalance,o2.totalBalance):Double.compare(o1.totalBalance,o2.totalBalance);
                            case MAXBALANCE:
                                return "DESC".equals(sortDirection)?-Double.compare(o1.maxBalance,o2.maxBalance):Double.compare(o1.maxBalance,o2.maxBalance);
                            case MINBALANCE:
                                return "DESC".equals(sortDirection)?-Double.compare(o1.minBalance,o2.minBalance):Double.compare(o1.minBalance,o2.minBalance);
                            default:
                                return "DESC".equals(sortDirection)?-o1.fullName.compareTo(o2.fullName):o1.fullName.compareTo(o2.fullName);
                        }
                    })).skip(ranges[i]).limit(ranges[i+1]).collect(Collectors.toCollection(ArrayList::new));

         assertEquals(queriedLocally.table.size(),gotTable.table.size());
                    for (int j=0; j<queriedLocally.table.size(); j++){
                        if(sortType.equals("FULLNAME")){
                            assertEquals(queriedLocally.table.get(j).fullName,gotTable.table.get(j).fullName);
                        }
                        if(sortType.equals("AGE")){
                            assertEquals(queriedLocally.table.get(j).age,gotTable.table.get(j).age);
                        }
                        if(sortType.equals("TOTALBALANCE")){
                            assertEquals(queriedLocally.table.get(j).totalBalance,gotTable.table.get(j).totalBalance);
                        }
                        if(sortType.equals("MINBALANCE")){
                            assertEquals(queriedLocally.table.get(j).minBalance,gotTable.table.get(j).minBalance);
                        }
                        if(sortType.equals("MAXBALANCE")){
                            assertEquals(queriedLocally.table.get(j).maxBalance,gotTable.table.get(j).maxBalance);
                        }

                    }
                }
            }
        }

      queriedLocally.table = myTable.table.stream()
        .filter((o1) -> filterPatronymic.toUpperCase()
          .equals(o1.fullName.split(" ")[o1.fullName.split(" ").length - 1].toUpperCase()))
        .sorted((o1, o2) -> o1.fullName.compareTo(o2.fullName))
        .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));


      assertEquals(queriedLocally.table.size(), gotTable.table.size());
      for (int i = 0; i < queriedLocally.size; i++) {
        assertEquals(queriedLocally.table.get(i).fullName, gotTable.table.get(i).fullName);
      }
    }

    for (String filterSurname : filterSurnames) {
      ClientRecordsToSend gotTable = tableRegister.get()
        .getClientRecords(0, 100, "ASC", "FULLNAME", "NAME", filterSurname);
      ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
      queriedLocally.table = myTable.table.stream()
        .filter(o1 -> filterSurname
          .matches(o1.fullName.split(" ")[0].toUpperCase()))
        .sorted((o1, o2) -> o1.fullName.compareTo(o2.fullName))
        .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));

      assertEquals(queriedLocally.table.size(), gotTable.table.size());
      for (int i = 0; i < queriedLocally.size; i++) {
        assertEquals(queriedLocally.table.get(i).fullName, gotTable.table.get(i).fullName);
      }
    }

  }


  private static class TestView implements ReportClientRecordsView {

    String contractNumber;

    Date contractDate;

    @Override

    public void start(String client, Date reportDate) throws Exception {
      this.contractNumber = contractNumber;
      this.contractDate = contractDate;

    }

    ;

    @Override
    public void append(ClientRecord row, int index) throws Exception {
      rowList.add(row);
    }

    ;


    @Override
    public void finish() throws Exception {

    }

    ;


    public final List<ClientRecord> rowList = Lists.newArrayList();

    public String clientName;
  }

  // TODO: неверный тест.
  @Test
  public void makeReportTest() throws Exception {
    TestView view = new TestView();
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = -1;
    clientRecord.totalBalance = 10;
    clientRecord.minBalance = 1;
    clientRecord.age = 9;
    clientRecord.maxBalance = 112;
    clientRecord.charm = "badboi";
    tableRegister.get().reportTest(clientRecord, 0, view);
    assertThat(view.rowList.get(0).equals(clientRecord));
  }

  @Test
  public void relativePath() {

    @Test
    public void makeReportTest() throws Exception{
        TestView view = new TestView();
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id=-1;
        clientRecord.totalBalance=10;
        clientRecord.minBalance=1;
        clientRecord.age=9;
        clientRecord.maxBalance=112;
        clientRecord.charm="BOI";
        tableRegister.get().reportTest(clientRecord,0,view);
        assertThat(view.rowList.get(0).equals(clientRecord));
    }

    @Test
    public void relativePath(){

    }


}
