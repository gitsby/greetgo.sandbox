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

import java.util.*;
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
            gotClients[i]=tableRegister.get().getExactClient(id);
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
                client.charm = null;
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
                client.charm = "";
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

        String[] moveByType = {"NOMOBILE","LESSTHAN11","LETTERS"};

        for(String option:moveByType){
            Client client =testDataGenerator.generateClient();
            if(option.equals("NOMOBILE")){
                for (int j = 0; j < client.phones.length ; j++) {
                    if(client.phones[j].phoneType==PhoneType.MOBILE){
                        client.phones[j].phoneType=PhoneType.WORK;
                    }
                }
            }
            if (option.equals("LESSTHAN11")){
                client.phones[0].number="123456";
            }
            if (option.equals("LETTERS")){
                client.phones[0].number="asdsada";
            }
            Integer id = tableRegister.get().createClient(client);
            assertThat(id).isEqualTo(-1);

        }


    }


    @Test
    public void getExactClientTest(){
        deleteAllData();

        Client client = testDataGenerator.generateClient();

        DbCharm dbCharm = testDataGenerator.generateRandomCharm(client.charm);
        tableTestDao.get().insertCharm(dbCharm);
        dbCharm.id = tableTestDao.get().getCharmId(dbCharm.name);

        DbClient dbClient = dbModelConverter.convertToDbClient(client,dbCharm.id);
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
        Client gotClient =tableRegister.get().getExactClient(clientId);
        assertThat(client.equals(gotClient));
    }

    @Test
    void getNullClientTest() {
        deleteAllData();
        Client gotClient =tableRegister.get().getExactClient(0);
        Client client = new Client();
        assertThat(client.equals(gotClient));
    }


    @Test
    void insertClientTest() {
        deleteAllData();
        Client client = testDataGenerator.generateClient();
        client.id = tableRegister.get().createClient(client);
        Client gotClient = tableRegister.get().getExactClient(client.id);
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

        Client gotClient = tableRegister.get().getExactClient(clientId);
        assertThat(gotClient.equals(client));
    }

    @Test
    public void deleteClientTest() {
        deleteAllData();
        Client client = testDataGenerator.generateClient();
        int clientId = tableRegister.get().createClient(client);
        tableRegister.get().deleteClient(clientId);
        client = tableRegister.get().getExactClient(clientId);
        assertThat(client.id==-1);
    }


    public void deleteAllData(){
        tableTestDao.get().deleteCharms();
        tableTestDao.get().deleteClientAccounts();
        tableTestDao.get().deleteClientAddrs();
        tableTestDao.get().deleteClients();
        tableTestDao.get().deletePhones();
        tableTestDao.get().charmSerialToStart();
        tableTestDao.get().clientAccountSerialToStart();
        tableTestDao.get().clientSerialToStart();
    }


    @Test
    public void deleteNullClientTest() throws Exception {
        deleteAllData();
        String ans = tableRegister.get().deleteClient(1);
        assertThat(("-1").equals(ans));
    }



    @Test
    public void updateNullClient() throws Exception {
        deleteAllData();
        int clientId = 1;
        Client client = testDataGenerator.generateClient();
        client.id=clientId;
        tableRegister.get().changeClient(client);
        Client updatedClient = tableRegister.get().getExactClient(clientId);
        assertThat(updatedClient.id==-1);
    }

    public void enterTheData() {
        deleteAllData();
        String namesJson = "{\"data\":[{\"surname\":\"Solovyov\",\"name\":\"Konstantin (Kostya)\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Trukhin\",\"name\":\"Pavel (Pasha)\",\"patronymic\":\"Victorovich\"},{\"surname\":\"Ryabtsev\",\"name\":\"Zhenka\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Siyasinov\",\"name\":\"Sergey (Seryozha)\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Siyankov\",\"name\":\"Yegor (Jora)\",\"patronymic\":\"Ruslanovich\"},{\"surname\":\"Yakimov\",\"name\":\"Artemiy\",\"patronymic\":\"Yakovich\"},{\"surname\":\"Steblev\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Yaroslavovich\"},{\"surname\":\"Shchegolyayev\",\"name\":\"Vladislav (Slava)\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Nardin\",\"name\":\"Sergei\",\"patronymic\":\"Igorevich\"},{\"surname\":\"Yumatov\",\"name\":\"Dionisiy\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Entsky\",\"name\":\"Maksim\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Susnin\",\"name\":\"Luchok\",\"patronymic\":\"Vladimirovich\"},{\"surname\":\"Yudachyov\",\"name\":\"Yevgeniy (Zhenya)\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Yermolovo\",\"name\":\"Ippolit\",\"patronymic\":\"Kirillovich\"},{\"surname\":\"Vanzin\",\"name\":\"Onufri\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Glukhov\",\"name\":\"Radoslav\",\"patronymic\":\"Alesnarovich\"},{\"surname\":\"Turov\",\"name\":\"Vasiliy (Vasya)\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Preobrazhensky\",\"name\":\"Slava\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Ipatyev\",\"name\":\"Alexei\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Kadnikov\",\"name\":\"Valentin (Valya)\",\"patronymic\":\"Pavlovich\"},{\"surname\":\"Kuzmich\",\"name\":\"Vyacheslav (Slava)\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Gulin\",\"name\":\"Ikovle\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Loginovsky\",\"name\":\"Isaak\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Barkov\",\"name\":\"Mili\",\"patronymic\":\"Andreevich\"},{\"surname\":\"Osin\",\"name\":\"Artem (Tyoma)\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Zuyev\",\"name\":\"Adam\",\"patronymic\":\"Leonidovich\"},{\"surname\":\"Uglitsky\",\"name\":\"Sergei\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Antipov\",\"name\":\"Valerian (Lera)\",\"patronymic\":\"Yermolayevich\"},{\"surname\":\"Petrov\",\"name\":\"Mili\",\"patronymic\":\"Dmitrievich\"},{\"surname\":\"Uralets\",\"name\":\"Miron\",\"patronymic\":\"Vsevolodovich\"},{\"surname\":\"Golovin\",\"name\":\"Artur\",\"patronymic\":\"Alesnarovich\"},{\"surname\":\"Shishov\",\"name\":\"Adam\",\"patronymic\":\"Konstantinovich\"},{\"surname\":\"Chudov\",\"name\":\"Nikolay (Kolya)\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Roshchin\",\"name\":\"Danya\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Chaadayev\",\"name\":\"Samuil\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Ryabkin\",\"name\":\"Timofei\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Osennykh\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Khanipov\",\"name\":\"Luka\",\"patronymic\":\"Yemelyanovich\"},{\"surname\":\"Manyakin\",\"name\":\"Demian\",\"patronymic\":\"Yevgenievich\"},{\"surname\":\"Leskov\",\"name\":\"Kusma (Kusya)\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Uglichinin\",\"name\":\"Kirill (Kirilka)\",\"patronymic\":\"Denisovich\"},{\"surname\":\"Markin\",\"name\":\"Josef\",\"patronymic\":\"Victorovich\"},{\"surname\":\"Valevach\",\"name\":\"Valeriy (Valera)\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Korablyov\",\"name\":\"Erik\",\"patronymic\":\"Denisovich\"},{\"surname\":\"Osolodkin\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Ilyich\"},{\"surname\":\"Koptsev\",\"name\":\"Sergei\",\"patronymic\":\"Vsevolodovich\"},{\"surname\":\"Lytkin\",\"name\":\"Lukyan\",\"patronymic\":\"Germanovich\"},{\"surname\":\"Tselner\",\"name\":\"Danya\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Lukin\",\"name\":\"Zigfrids\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Mishutin\",\"name\":\"Petr\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Yushakov\",\"name\":\"Pyotr (Petya)\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Uvarov\",\"name\":\"Zakhar (Zakharik)\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Volikov\",\"name\":\"Yakov (Yasha)\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Anrep\",\"name\":\"Jaromir\",\"patronymic\":\"Innokentievich\"},{\"surname\":\"Tredyakovsky\",\"name\":\"Oleg (Olezhka)\",\"patronymic\":\"Zakharovich\"},{\"surname\":\"Tychkin\",\"name\":\"Anton (Antosha)\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Pavlov\",\"name\":\"Adam\",\"patronymic\":\"Valeryevich\"},{\"surname\":\"Buturovich\",\"name\":\"Larion (Larya)\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Khmelnov\",\"name\":\"Robert\",\"patronymic\":\"Yegorovich\"},{\"surname\":\"Penkin\",\"name\":\"Krasimir\",\"patronymic\":\"Anatolievich\"},{\"surname\":\"Shulgin\",\"name\":\"Filipp (Filya)\",\"patronymic\":\"Savelievich\"},{\"surname\":\"Votyakov\",\"name\":\"Isaak\",\"patronymic\":\"Makarovich\"},{\"surname\":\"Sabitov\",\"name\":\"Aleksandr (Sasha)\",\"patronymic\":\"Vitalievich\"},{\"surname\":\"Dresvyanin\",\"name\":\"Foma\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Vorontsov\",\"name\":\"Ippolit\",\"patronymic\":\"Mikhailovich\"},{\"surname\":\"Subotin\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Valentinovich\"},{\"surname\":\"Kasharin\",\"name\":\"Isaak\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Revyakin\",\"name\":\"Panteley\",\"patronymic\":\"Romanovich\"},{\"surname\":\"Kosaryov\",\"name\":\"Boris (Borya)\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Kravchuk\",\"name\":\"Radoslav\",\"patronymic\":\"Timurovich\"},{\"surname\":\"Muravyov\",\"name\":\"Ilya (Ilik)\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Izyumov\",\"name\":\"Boleslaw\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Dudko\",\"name\":\"Fridrik\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Yasenev\",\"name\":\"Jaromir\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Lyovkin\",\"name\":\"Karl\",\"patronymic\":\"Romanovich\"},{\"surname\":\"Gorelov\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Savelievich\"},{\"surname\":\"Fomin\",\"name\":\"Robert\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Romanov\",\"name\":\"Tsezar\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Ulyanin\",\"name\":\"Demian\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Yazov\",\"name\":\"Lavro\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Ruchkin\",\"name\":\"Vissarion\",\"patronymic\":\"Dmitrievich\"},{\"surname\":\"Vyrypayev\",\"name\":\"Androniki\",\"patronymic\":\"Valeryevich\"},{\"surname\":\"Shirinov\",\"name\":\"Radoslav\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Guskov\",\"name\":\"Gotfrid\",\"patronymic\":\"Germanovich\"},{\"surname\":\"Legkodimov\",\"name\":\"Roman (Roma)\",\"patronymic\":\"Yanovich\"},{\"surname\":\"Mukhomorov\",\"name\":\"Anatoliy (Tolya)\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Lapotnikov\",\"name\":\"Kvetoslav\",\"patronymic\":\"Vladimirovich\"},{\"surname\":\"Khantsev\",\"name\":\"Nikolay (Kolya)\",\"patronymic\":\"Mikhailovich\"},{\"surname\":\"Ilyushin\",\"name\":\"Lukyan\",\"patronymic\":\"Gennadiyevich\"},{\"surname\":\"Zolotov\",\"name\":\"Christov\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Ardankin\",\"name\":\"Alexei\",\"patronymic\":\"Andreevich\"},{\"surname\":\"Ivanov\",\"name\":\"Miron\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Marinkin\",\"name\":\"Gavrila\",\"patronymic\":\"Anatolievich\"},{\"surname\":\"Razin\",\"name\":\"Vyacheslav (Slava)\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Krupnov\",\"name\":\"Fridrik\",\"patronymic\":\"Larionovich\"},{\"surname\":\"Yenotov\",\"name\":\"Vikentiy\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Astankov\",\"name\":\"Nil\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Tsvilenev\",\"name\":\"Samuil\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Sharshin\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Ivanovich\"},{\"surname\":\"Tsvetnov\",\"name\":\"Dionisiy\",\"patronymic\":\"Andreevich\"}]}";
        Gson gson = new Gson();
        Names names = gson.fromJson(namesJson,Names.class);
        int[] clientids = new int[100];
        int[] accountids = new int[200];
        int[] mins = new int[100];
        int[] maxs = new int[100];
        for (int i = 0; i <100 ; i++) {
            mins[i]=RND.plusInt(1000);
            maxs[i]=1000+RND.plusInt(1000);
        }
        for (int i = 0; i < 100; i++) {
            Client client = testDataGenerator.generateClient();
            client.name = names.data[i].name;
            client.surname = names.data[i].surname;
            client.patronymic= names.data[i].patronymic;
            clientids[i] = tableRegister.get().createClient(client);
            Integer[] ids = tableDao.get().getAccount(clientids[i]);
            accountids[i*2]=ids[0];
            accountids[i*2+1]=ids[1];

            DbClientAccount dbClientAccount = new DbClientAccount();
            dbClientAccount.money = mins[i]+0.0f;
            dbClientAccount.client = clientids[i];
            dbClientAccount.id = accountids[i*2];
            tableDao.get().updateAccount(dbClientAccount);
            dbClientAccount.money = maxs[i]+0.0f;
            dbClientAccount.client = clientids[i];
            dbClientAccount.id = accountids[i*2+1];
            tableDao.get().updateAccount(dbClientAccount);
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
                            case CHARM:
                                return "DESC".equals(sortDirection)?-o1.charm.compareTo(o2.charm):o1.charm.compareTo(o2.charm);
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



        for (String filterName: filterNames){
            ClientRecordsToSend gotTable = tableRegister.get()
                    .getClientRecords(0,100,"ASC","FULLNAME","NAME",filterName);
            ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
            queriedLocally.table = myTable.table.stream()
                    .filter(o1 -> filterName
                                    .toUpperCase().equals(o1.fullName.split(" ")[1].toUpperCase()))
                    .sorted((o1,o2) -> o1.fullName.compareTo(o2.fullName))
                    .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));


            assertEquals(queriedLocally.table.size(),gotTable.table.size());
            for(int i=0; i<queriedLocally.size; i++){
                assertEquals(queriedLocally.table.get(i).fullName,gotTable.table.get(i).fullName);
            }
        }

        for (String filterPatronymic: filterPatronymics){
            ClientRecordsToSend gotTable = tableRegister.get()
                    .getClientRecords(0,100,"ASC","FULLNAME","PATRONYMIC",filterPatronymic);
            ClientRecordsToSend queriedLocally = new ClientRecordsToSend();

            queriedLocally.table = myTable.table.stream()
                    .filter((o1) -> filterPatronymic.toUpperCase()
                            .equals(o1.fullName.split(" ")[o1.fullName.split(" ").length-1].toUpperCase()))
                    .sorted((o1,o2) -> o1.fullName.compareTo(o2.fullName))
                    .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));



            assertEquals(queriedLocally.table.size(),gotTable.table.size());
            for(int i=0; i<queriedLocally.size; i++){
                assertEquals(queriedLocally.table.get(i).fullName,gotTable.table.get(i).fullName);
            }
        }

        for (String filterSurname: filterSurnames){
            ClientRecordsToSend gotTable = tableRegister.get()
                    .getClientRecords(0,100,"ASC","FULLNAME","NAME",filterSurname);
            ClientRecordsToSend queriedLocally = new ClientRecordsToSend();
            queriedLocally.table = myTable.table.stream()
                    .filter(o1 -> filterSurname
                            .matches(o1.fullName.split(" ")[0].toUpperCase()))
                    .sorted((o1,o2) -> o1.fullName.compareTo(o2.fullName))
                    .skip(0).limit(100).collect(Collectors.toCollection(ArrayList::new));

            assertEquals(queriedLocally.table.size(),gotTable.table.size());
            for(int i=0; i<queriedLocally.size; i++){
                assertEquals(queriedLocally.table.get(i).fullName,gotTable.table.get(i).fullName);
            }
        }

    }



    private static class TestView implements ReportClientRecordsView {

        String contractNumber;

        Date contractDate;

        @Override

        public void start(String client, Date reportDate)throws Exception{
            this.contractNumber = contractNumber;
            this.contractDate = contractDate;

        };
        @Override
        public void append(ClientRecord row, int index)throws Exception{
            rowList.add(row);
        };


        @Override
        public void finish()throws Exception{

        };



        public final List<ClientRecord> rowList = Lists.newArrayList();

        public String clientName;
    }

    @Test
    public void makeReportTest() throws Exception{
        TestView view = new TestView();
        ClientRecord clientRecord = new ClientRecord();
        clientRecord.id=-1;
        clientRecord.totalBalance=10;
        clientRecord.minBalance=1;
        clientRecord.age=9;
        clientRecord.maxBalance=112;
        clientRecord.charm="badboi";
        tableRegister.get().reportTest(clientRecord,0,view);
        assertThat(view.rowList.get(0).equals(clientRecord));
    }

}
