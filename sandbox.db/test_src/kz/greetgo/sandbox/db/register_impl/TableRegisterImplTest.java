package kz.greetgo.sandbox.db.register_impl;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.db.dao.TableDao;
import kz.greetgo.sandbox.db.test.dao.TableTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;

import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;


public class TableRegisterImplTest extends ParentTestNg{
    public BeanGetter<TableRegister> tableRegister;
    public BeanGetter<TableTestDao> tableTestDao;
    public BeanGetter<TableDao> tableDao;

    TestDataGenerator testDataGenerator = new TestDataGenerator();
    DbModelConverter dbModelConverter = new DbModelConverter();

    @Test
    public void insertUserX10Test(){
        deleteAllData();
        User[] sentUsers = new User[10];
        User[] gotUsers = new User[10];

        for(int i=0; i<10; i++) {
            sentUsers[i]=testDataGenerator.generateUser();
            int id = tableRegister.get().createUser(sentUsers[i]);
            sentUsers[i].id=id;
            gotUsers[i]=tableRegister.get().getExactUser(id);
        }

        for(int i=0; i<10; i++){
            assertThat(sentUsers[i].equals(gotUsers[i]));
        }
    }

    @Test
    public void insertUsersWithNullParsTest(){
        deleteAllData();
        String[] move  = {"name","surname","gender","charm","registeredAddress","birthDate"};
        for(String option: move) {
            User user = testDataGenerator.generateUser();
            if(option.equals("name")){
                user.name=null;
            } else if(option.equals("surname")){
                user.surname=null;
            } else if(option.equals("gender")){
                user.genderType = null;
            } else if(option.equals("charm")){
                user.charm = null;
            } else if(option.equals("registeredAddress")){
                user.registeredAddress = null;
            } else if(option.equals("birthDate")){
                user.birthDate = null;
            }
            Integer id = tableRegister.get().createUser(user);
            assertThat(id==-1);
        }
        for(String option: move) {
            User user=testDataGenerator.generateUser();
            if(option.equals("name")){
                user.name="";
            } else if(option.equals("surname")){
                user.surname="";
            } else if(option.equals("gender")){
                user.genderType = null;
            } else if(option.equals("charm")){
                user.charm = "";
            } else if(option.equals("registeredAddress")){
                user.registeredAddress.house = "";
                user.registeredAddress.flat = "";
                user.registeredAddress.street = "";
            } else if(option.equals("birthDate")){
                user.birthDate = null;
            }
            Integer id = tableRegister.get().createUser(user);
            assertThat(id==-1);
        }

        String[] moveByType = {"NOMOBILE","LESSTHAN11","LETTERS"};

        for(String option:moveByType){
            User user=testDataGenerator.generateUser();
            if(option.equals("NOMOBILE")){
                for (int j = 0; j < user.phones.length ; j++) {
                    if(user.phones[j].phoneType==PhoneType.MOBILE){
                        user.phones[j].phoneType=PhoneType.WORK;
                    }
                }
            }
            if (option.equals("LESSTHAN11")){
                user.phones[0].number="123456";
            }
            if (option.equals("LETTERS")){
                user.phones[0].number="asdsada";
            }
            Integer id = tableRegister.get().createUser(user);
            assertThat(id).isEqualTo(-1);

        }


    }


    @Test
    public void getExactClientTest(){
        deleteAllData();

        User user = testDataGenerator.generateUser();

        DbCharm dbCharm = testDataGenerator.generateRandomCharm(user.charm);
        tableTestDao.get().insertCharm(dbCharm);
        dbCharm.id = tableTestDao.get().getCharmId(dbCharm.name);

        DbClient dbClient = dbModelConverter.convertToDbClient(user,dbCharm.id);
        tableTestDao.get().insertClient(dbClient);
        Integer userID=tableTestDao.get().getLastClientID();
        user.id=userID;
        DbClientPhone[] dbClientPhones = dbModelConverter.convertToDbClientPhones(user);
        DbClientAddress dbClientAddressFactual = dbModelConverter.convertToDbClientAddressFactual(user);
        DbClientAddress dbClientAddressRegistered = dbModelConverter.convertToDbClientAddressRegistered(user);
        tableTestDao.get().insertAddress(dbClientAddressFactual);
        tableTestDao.get().insertAddress(dbClientAddressRegistered);
        for (DbClientPhone dbClientPhone: dbClientPhones) {
            tableTestDao.get().insertPhone(dbClientPhone);
        }
        User gotUser =tableRegister.get().getExactUser(userID);
        assertThat(user.equals(gotUser));
    }

    @Test
    void getNullClientTest() {
        deleteAllData();
        User gotUser =tableRegister.get().getExactUser(0);
        User user = new User();
        assertThat(user.equals(gotUser));
    }


    @Test
    void insertUserTest() {
        deleteAllData();
        User user = testDataGenerator.generateUser();
        user.id = tableRegister.get().createUser(user);
        User gotUser = tableRegister.get().getExactUser(user.id);
        assertThat(user.equals(gotUser));
    }

    @Test
    public void updateUserTest() throws Exception {
        deleteAllData();
        User user = testDataGenerator.generateUser();
        int userID = tableRegister.get().createUser(user);
        user = testDataGenerator.generateUser();
        user.id = userID;
        tableRegister.get().changeUser(user);

        User gotUser = tableRegister.get().getExactUser(userID);
        assertThat(gotUser.equals(user));
    }

    @Test
    public void deleteUserTest() {
        deleteAllData();
        User user = testDataGenerator.generateUser();
        int userID = tableRegister.get().createUser(user);
        tableRegister.get().deleteUser(userID);
        user = tableRegister.get().getExactUser(userID);
        assertThat(user.id==-1);
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
    public void deleteNullUserTest() throws Exception {
        deleteAllData();
        String ans = tableRegister.get().deleteUser(1);
        assertThat(("-1").equals(ans));
    }



    @Test
    public void updateNullUser() throws Exception {
        deleteAllData();
        int userID = 1;
        User user = testDataGenerator.generateUser();
        user.id=userID;
        tableRegister.get().changeUser(user);
        User updatedUser = tableRegister.get().getExactUser(userID);
        assertThat(updatedUser.id==-1);
    }

    public void enterTheData() {
        deleteAllData();
        String namesJson = "{\"data\":[{\"surname\":\"Solovyov\",\"name\":\"Konstantin (Kostya)\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Trukhin\",\"name\":\"Pavel (Pasha)\",\"patronymic\":\"Victorovich\"},{\"surname\":\"Ryabtsev\",\"name\":\"Zhenka\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Siyasinov\",\"name\":\"Sergey (Seryozha)\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Siyankov\",\"name\":\"Yegor (Jora)\",\"patronymic\":\"Ruslanovich\"},{\"surname\":\"Yakimov\",\"name\":\"Artemiy\",\"patronymic\":\"Yakovich\"},{\"surname\":\"Steblev\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Yaroslavovich\"},{\"surname\":\"Shchegolyayev\",\"name\":\"Vladislav (Slava)\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Nardin\",\"name\":\"Sergei\",\"patronymic\":\"Igorevich\"},{\"surname\":\"Yumatov\",\"name\":\"Dionisiy\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Entsky\",\"name\":\"Maksim\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Susnin\",\"name\":\"Luchok\",\"patronymic\":\"Vladimirovich\"},{\"surname\":\"Yudachyov\",\"name\":\"Yevgeniy (Zhenya)\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Yermolovo\",\"name\":\"Ippolit\",\"patronymic\":\"Kirillovich\"},{\"surname\":\"Vanzin\",\"name\":\"Onufri\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Glukhov\",\"name\":\"Radoslav\",\"patronymic\":\"Alesnarovich\"},{\"surname\":\"Turov\",\"name\":\"Vasiliy (Vasya)\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Preobrazhensky\",\"name\":\"Slava\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Ipatyev\",\"name\":\"Alexei\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Kadnikov\",\"name\":\"Valentin (Valya)\",\"patronymic\":\"Pavlovich\"},{\"surname\":\"Kuzmich\",\"name\":\"Vyacheslav (Slava)\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Gulin\",\"name\":\"Ikovle\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Loginovsky\",\"name\":\"Isaak\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Barkov\",\"name\":\"Mili\",\"patronymic\":\"Andreevich\"},{\"surname\":\"Osin\",\"name\":\"Artem (Tyoma)\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Zuyev\",\"name\":\"Adam\",\"patronymic\":\"Leonidovich\"},{\"surname\":\"Uglitsky\",\"name\":\"Sergei\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Antipov\",\"name\":\"Valerian (Lera)\",\"patronymic\":\"Yermolayevich\"},{\"surname\":\"Petrov\",\"name\":\"Mili\",\"patronymic\":\"Dmitrievich\"},{\"surname\":\"Uralets\",\"name\":\"Miron\",\"patronymic\":\"Vsevolodovich\"},{\"surname\":\"Golovin\",\"name\":\"Artur\",\"patronymic\":\"Alesnarovich\"},{\"surname\":\"Shishov\",\"name\":\"Adam\",\"patronymic\":\"Konstantinovich\"},{\"surname\":\"Chudov\",\"name\":\"Nikolay (Kolya)\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Roshchin\",\"name\":\"Danya\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Chaadayev\",\"name\":\"Samuil\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Ryabkin\",\"name\":\"Timofei\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Osennykh\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Nikitovich\"},{\"surname\":\"Khanipov\",\"name\":\"Luka\",\"patronymic\":\"Yemelyanovich\"},{\"surname\":\"Manyakin\",\"name\":\"Demian\",\"patronymic\":\"Yevgenievich\"},{\"surname\":\"Leskov\",\"name\":\"Kusma (Kusya)\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Uglichinin\",\"name\":\"Kirill (Kirilka)\",\"patronymic\":\"Denisovich\"},{\"surname\":\"Markin\",\"name\":\"Josef\",\"patronymic\":\"Victorovich\"},{\"surname\":\"Valevach\",\"name\":\"Valeriy (Valera)\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Korablyov\",\"name\":\"Erik\",\"patronymic\":\"Denisovich\"},{\"surname\":\"Osolodkin\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Ilyich\"},{\"surname\":\"Koptsev\",\"name\":\"Sergei\",\"patronymic\":\"Vsevolodovich\"},{\"surname\":\"Lytkin\",\"name\":\"Lukyan\",\"patronymic\":\"Germanovich\"},{\"surname\":\"Tselner\",\"name\":\"Danya\",\"patronymic\":\"Tarasovich\"},{\"surname\":\"Lukin\",\"name\":\"Zigfrids\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Mishutin\",\"name\":\"Petr\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Yushakov\",\"name\":\"Pyotr (Petya)\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Uvarov\",\"name\":\"Zakhar (Zakharik)\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Volikov\",\"name\":\"Yakov (Yasha)\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Anrep\",\"name\":\"Jaromir\",\"patronymic\":\"Innokentievich\"},{\"surname\":\"Tredyakovsky\",\"name\":\"Oleg (Olezhka)\",\"patronymic\":\"Zakharovich\"},{\"surname\":\"Tychkin\",\"name\":\"Anton (Antosha)\",\"patronymic\":\"Vyacheslavovich\"},{\"surname\":\"Pavlov\",\"name\":\"Adam\",\"patronymic\":\"Valeryevich\"},{\"surname\":\"Buturovich\",\"name\":\"Larion (Larya)\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Khmelnov\",\"name\":\"Robert\",\"patronymic\":\"Yegorovich\"},{\"surname\":\"Penkin\",\"name\":\"Krasimir\",\"patronymic\":\"Anatolievich\"},{\"surname\":\"Shulgin\",\"name\":\"Filipp (Filya)\",\"patronymic\":\"Savelievich\"},{\"surname\":\"Votyakov\",\"name\":\"Isaak\",\"patronymic\":\"Makarovich\"},{\"surname\":\"Sabitov\",\"name\":\"Aleksandr (Sasha)\",\"patronymic\":\"Vitalievich\"},{\"surname\":\"Dresvyanin\",\"name\":\"Foma\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Vorontsov\",\"name\":\"Ippolit\",\"patronymic\":\"Mikhailovich\"},{\"surname\":\"Subotin\",\"name\":\"Ruslan (Rusya)\",\"patronymic\":\"Valentinovich\"},{\"surname\":\"Kasharin\",\"name\":\"Isaak\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Revyakin\",\"name\":\"Panteley\",\"patronymic\":\"Romanovich\"},{\"surname\":\"Kosaryov\",\"name\":\"Boris (Borya)\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Kravchuk\",\"name\":\"Radoslav\",\"patronymic\":\"Timurovich\"},{\"surname\":\"Muravyov\",\"name\":\"Ilya (Ilik)\",\"patronymic\":\"Petrovich\"},{\"surname\":\"Izyumov\",\"name\":\"Boleslaw\",\"patronymic\":\"Vladislavovich\"},{\"surname\":\"Dudko\",\"name\":\"Fridrik\",\"patronymic\":\"Vadimovich\"},{\"surname\":\"Yasenev\",\"name\":\"Jaromir\",\"patronymic\":\"Timofeyevich\"},{\"surname\":\"Lyovkin\",\"name\":\"Karl\",\"patronymic\":\"Romanovich\"},{\"surname\":\"Gorelov\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Savelievich\"},{\"surname\":\"Fomin\",\"name\":\"Robert\",\"patronymic\":\"Artemovich\"},{\"surname\":\"Romanov\",\"name\":\"Tsezar\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Ulyanin\",\"name\":\"Demian\",\"patronymic\":\"Filippovich\"},{\"surname\":\"Yazov\",\"name\":\"Lavro\",\"patronymic\":\"Rodionovich\"},{\"surname\":\"Ruchkin\",\"name\":\"Vissarion\",\"patronymic\":\"Dmitrievich\"},{\"surname\":\"Vyrypayev\",\"name\":\"Androniki\",\"patronymic\":\"Valeryevich\"},{\"surname\":\"Shirinov\",\"name\":\"Radoslav\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Guskov\",\"name\":\"Gotfrid\",\"patronymic\":\"Germanovich\"},{\"surname\":\"Legkodimov\",\"name\":\"Roman (Roma)\",\"patronymic\":\"Yanovich\"},{\"surname\":\"Mukhomorov\",\"name\":\"Anatoliy (Tolya)\",\"patronymic\":\"Sergeyevich\"},{\"surname\":\"Lapotnikov\",\"name\":\"Kvetoslav\",\"patronymic\":\"Vladimirovich\"},{\"surname\":\"Khantsev\",\"name\":\"Nikolay (Kolya)\",\"patronymic\":\"Mikhailovich\"},{\"surname\":\"Ilyushin\",\"name\":\"Lukyan\",\"patronymic\":\"Gennadiyevich\"},{\"surname\":\"Zolotov\",\"name\":\"Christov\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Ardankin\",\"name\":\"Alexei\",\"patronymic\":\"Andreevich\"},{\"surname\":\"Ivanov\",\"name\":\"Miron\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Marinkin\",\"name\":\"Gavrila\",\"patronymic\":\"Anatolievich\"},{\"surname\":\"Razin\",\"name\":\"Vyacheslav (Slava)\",\"patronymic\":\"Stanislavovich\"},{\"surname\":\"Krupnov\",\"name\":\"Fridrik\",\"patronymic\":\"Larionovich\"},{\"surname\":\"Yenotov\",\"name\":\"Vikentiy\",\"patronymic\":\"Valerianovich\"},{\"surname\":\"Astankov\",\"name\":\"Nil\",\"patronymic\":\"Olegovich\"},{\"surname\":\"Tsvilenev\",\"name\":\"Samuil\",\"patronymic\":\"Borisovich\"},{\"surname\":\"Sharshin\",\"name\":\"Denis (Deniska)\",\"patronymic\":\"Ivanovich\"},{\"surname\":\"Tsvetnov\",\"name\":\"Dionisiy\",\"patronymic\":\"Andreevich\"}]}";
        Gson gson = new Gson();
        Names names = gson.fromJson(namesJson,Names.class);
        int[] userids = new int[100];
        int[] accountids = new int[200];
        int[] mins = new int[100];
        int[] maxs = new int[100];
        for (int i = 0; i <100 ; i++) {
            mins[i]=RND.plusInt(1000);
            maxs[i]=1000+RND.plusInt(1000);
        }
        for (int i = 0; i < 100; i++) {
            User user = testDataGenerator.generateUser();
            user.name = names.data[i].name;
            user.surname = names.data[i].surname;
            user.patronymic= names.data[i].patronymic;
            userids[i] = tableRegister.get().createUser(user);
            Integer[] ids = tableDao.get().getAccount(userids[i]);
            accountids[i*2]=ids[0];
            accountids[i*2+1]=ids[1];

            DbClientAccount dbClientAccount = new DbClientAccount();
            dbClientAccount.money = mins[i]+0.0f;
            dbClientAccount.client = userids[i];
            dbClientAccount.id = accountids[i*2];
            tableDao.get().updateAccount(dbClientAccount);
            dbClientAccount.money = maxs[i]+0.0f;
            dbClientAccount.client = userids[i];
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
        TableToSend myTable = tableRegister.get().getTableData(0,100,"ASC","FULLNAME","NAME","");

        String[] sortDirections = {"ASC","DESC"};
        String[] sortTypes = {"FULLNAME","AGE","TOTALBALANCE","MINBALANCE","MAXBALANCE"};
        int[] ranges = {0,33,66,100};

        String[] filterNames = {"adam","denis","isaak"};
        String[] filterPatronymics = {"Vladimirovich","Timofeyevich","Victorovich"};
        String[] filterSurnames = {"TS([\\w]*?)V","SIY([\\w]*?)V","CH([\\w]*?)V"};


        for(String sortDirection: sortDirections){
            for(String sortType: sortTypes){
                for(int i=0; i<ranges.length-1; i++){
                    TableToSend gotTable = tableRegister.get().getTableData(ranges[i],ranges[i+1],sortDirection, sortType,"NAME","");
                    TableToSend queriedLocally = new TableToSend();
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
            TableToSend gotTable = tableRegister.get()
                    .getTableData(0,100,"ASC","FULLNAME","NAME",filterName);
            TableToSend queriedLocally = new TableToSend();
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
            TableToSend gotTable = tableRegister.get()
                    .getTableData(0,100,"ASC","FULLNAME","PATRONYMIC",filterPatronymic);
            TableToSend queriedLocally = new TableToSend();

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
            TableToSend gotTable = tableRegister.get()
                    .getTableData(0,100,"ASC","FULLNAME","NAME",filterSurname);
            TableToSend queriedLocally = new TableToSend();
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

}
