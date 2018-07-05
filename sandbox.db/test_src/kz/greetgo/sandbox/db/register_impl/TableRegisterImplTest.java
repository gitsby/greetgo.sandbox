package kz.greetgo.sandbox.db.register_impl;

import com.google.gson.Gson;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.IllegalLoginOrPassword;
import kz.greetgo.sandbox.controller.errors.NoAccountName;
import kz.greetgo.sandbox.controller.errors.NoPassword;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.db.dao.TableDao;
import kz.greetgo.sandbox.db.test.dao.TableTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.apache.ibatis.jdbc.SQL;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;


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
            System.out.println(sentUsers[i].toString() + " " + gotUsers[i].toString());
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
            assertThat(id).isEqualTo(-1);
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
        System.err.println(user.toString());
        user = testDataGenerator.generateUser();
        System.err.println(user.toString());
        user.id = userID;
        tableRegister.get().changeUser(user);
        System.err.println("GOTYA");
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

    @Test
    public void enterTheDataNotTest() {
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
            System.err.println(user.toString());
            userids[i] = tableRegister.get().createUser(user);
            System.err.println(userids[i]);
            Integer[] ids = tableDao.get().getAccount(userids[i]);
            System.err.println(ids.length);
            accountids[i*2]=ids[0];
            accountids[i*2+1]=ids[1];

            DbClientAccount dbClientAccount = new DbClientAccount();
            dbClientAccount.money = mins[i]+0.0f;
            dbClientAccount.client = userids[i];
            dbClientAccount.id = accountids[i*2];
            System.err.println(dbClientAccount.toString());
            tableDao.get().updateAccount(dbClientAccount);
            dbClientAccount.money = maxs[i]+0.0f;
            dbClientAccount.client = userids[i];
            dbClientAccount.id = accountids[i*2+1];
            System.err.println(dbClientAccount.toString());
            tableDao.get().updateAccount(dbClientAccount);
        }




    }


    @Test
    void tableFunctionalityTest(){
        TableToSend tableToSend = tableRegister.get().getTableData(0,8,"ASC","FULLNAME","SURNAME","Pavlov");
        for (TableModel tableModel:
             tableToSend.table
                ) {
            System.err.println(tableModel.toString());
        }

        String[] sortDirections = {"ASC","DESC"};
        String[] sortTypes = {"FULLNAME","AGE","TOTALBALANCE","MINBALANCE","MAXBALANCE"};
        int[] ranges = {0,33,66,100};
        String[] filterTypes = {"NAME","SURNAME","PATRONYMIC"};
        String[] filterNames = {"adam","denis","isaak"};
        String[] filterPatronymic = {"Vladimirovich","Timofeyevich","Victorovich"};
        String[] filterSurnames = {"Ts%v","Siy%v","ch%v"};



    }


//    @Test
//    public void getTableToView(){
//        deleteAllData();
//        String[] sortTypes={"FULLNAME","MINBALANCE","MAXBALANCE","TOTALBALANCE","AGE","BALANCE"};
//
//    }
//
//    @Test
//    public void getNullTableToView(){
//        deleteAllData();
//    }







    /**
     *
     * Methods to test:
     *
     *   - TableToSend getTableData(int skipNumber, int limit, String sortDirection, String sortType);
     *   - User getExactUser(String userID);
     *   - String getLastId();
     *   - String createUser(User user);
     *   - String changeUser(User user);
     *   - String deleteUser(String userID);
     *   - Boolean checkIfThereUser(String userID);
     *
     *
     * @DDB => Delete Data Before
     *
     *
     * @DDB
     * Just Update User Record =>  {
     *     - get random user from table of clients
     *     - update that record with random data
     *     - check for correspondence
     * }
     *
     * @DDB
     * Null Update User Record => {
     *     - get random user from table of clients
     *     - update user with null data(for not null columns check each)
     *     - check for correspondence
     * }
     *
     * @DDB
     * Just Insert Tons Of Users => {
     *     - create random user x 1000
     *     - make insertion calls x 1000 & collect id's
     * sql - recreate users via get call to db using collected id's
     *     - check for correspondence of id's
     * }
     *
     * @DDB
     * Null Insert Of User => {
     *     - create random user x 1
     *     - insert user with null data(for not null columns check each)
     * sql - check for nonexistence
     * }
     *
     *
     *
     * @DDB
     * Just Delete Tons Of Users => {
     * sql - insert random user x 1000
     *     - delete them by own generated id's
     *     - check for existence of them
     * }
     *
     * @DDB
     * Null Delete Of User => {
     * sql - insert random user x 1
     *     - delete null | wrong id
     *     - check for existence of such id
     * }
     *
     *
     * @DDB
     * Just Get Table Of Random Requirements => {
     * sql - create random user x 1000
     * sql - create corresponding accounts x 5000 ~ each User 5 accounts
     *     - create random filter
     *     - require table
     *     - check for correspondence
     * }
     *
     * @DDB
     * Null Get Table of Random Requirements => {
     * sql - create random user x 1000
     * sql - create corresponding accounts x 5000 ~ each User 5 accounts
     *     - create null filter
     *     - require table
     *     - check for correspondence
     * }
     *
     *
     * */

}
