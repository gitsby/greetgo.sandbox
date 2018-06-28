package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.errors.IllegalLoginOrPassword;
import kz.greetgo.sandbox.controller.errors.NoAccountName;
import kz.greetgo.sandbox.controller.errors.NoPassword;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;
import kz.greetgo.sandbox.db.test.dao.TableTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.apache.ibatis.jdbc.SQL;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


public class TableRegisterTest extends ParentTestNg{
    public BeanGetter<TableRegister> tableRegister;
    public BeanGetter<TableTestDao> tableTestDao;

    TestDataGenerator testDataGenerator = new TestDataGenerator();

    @Test
    public void insertUserX10(){
        deleteAllData();
        User[] sentUsers = new User[10];
        User[] gotUsers = new User[10];

        for(int i=0; i<10; i++) {
            sentUsers[i]=testDataGenerator.generateUser();
            int id = tableRegister.get().createUser(sentUsers[i]);
            sentUsers[i].id=id;
            gotUsers[i]=tableTestDao.get().getExactClient(id);
        }

        assertThat(gotUsers).contains(sentUsers);
    }

    @Test
    public void insertUsersWithNullPars(){
        deleteAllData();
        User[] sentUsers = new User[10];
        User[] gotUsers = new User[10];
        String[] move  = {"name","surname","gender","charm","registeredAddress","mobile"};
        int i=0;
        for(String option: move) {
            sentUsers[i]=testDataGenerator.generateUser();
            if(option.equals("name")){
                sentUsers[i].name=null;
            } else if(option.equals("surname")){
                sentUsers[i].surname=null;
            } else if(option.equals("gender")){
                sentUsers[i].genderType = null;
            } else if(option.equals("charm")){
                sentUsers[i].charm = null;
            } else if(option.equals("registeredAddress")){
                sentUsers[i].registeredAddress = null;
            } else if(option.equals("mobile")){
                sentUsers[i].phones = null;
            }

            Integer id = tableRegister.get().createUser(sentUsers[i]);
            assertThat(id).isNull();
        }

    }

    @Test
    public void deleteUserX10(){
        deleteAllData();
        User[] sentUsers = new User[10];
        for (int i=0; i<10; i++){
            sentUsers[i]=testDataGenerator.generateUser();
            String name = sentUsers[i].name;
            Phone[] phones = sentUsers[i].phones;
            Address fact = sentUsers[i].factualAddress;
            Address reg = sentUsers[i].registeredAddress;
            String charm = sentUsers[i].charm.toString();
            String surname = sentUsers[i].surname;
            String patronymic = sentUsers[i].patronymic;
            String gender = sentUsers[i].genderType.toString();
            tableTestDao.get().insertCharm(charm, charm, 225);
            tableTestDao.get().insertClient(name,charm, surname,gender,patronymic);
            int userID = tableTestDao.get().getLastClientID();
            for(int j=0; j<phones.length; j++){
                tableTestDao.get().insertPhone(userID, phones[j].number, phones[j].phoneType.toString());
            }
            tableTestDao.get().insertAddress(userID,"FACT",fact.street, fact.house, fact.flat);
            tableTestDao.get().insertAddress(userID,"REG",reg.street, reg.house, reg.flat);
            sentUsers[i].id=userID;
            tableRegister.get().deleteUser(userID);
        }
        for(int i=0;i<10; i++){
                assertThat(tableTestDao.get().getExactClient(sentUsers[i].id)).isNull();
        }
    }


    @Test
    public void getExactClientTest() throws Exception {
        deleteAllData();
        User user = testDataGenerator.generateUser();
        tableTestDao.get().insertCharm(user.charm,user.charm,255);
        tableTestDao.get().insertClient(user.name,user.charm,user.surname,user.genderType.toString(),user.patronymic);
        int userID = tableTestDao.get().getLastClientID();
        user.id=userID;
        for(int j=0; j<user.phones.length; j++){
            tableTestDao.get().insertPhone(userID, user.phones[j].number, user.phones[j].phoneType.toString());
        }
        tableTestDao.get().insertAddress(userID,"FACT",user.factualAddress.street, user.factualAddress.house, user.factualAddress.flat);
        tableTestDao.get().insertAddress(userID,"REG",user.registeredAddress.street, user.registeredAddress.house, user.registeredAddress.flat);

        User gotUser = tableRegister.get().getExactUser(userID);
        assertThat(user).isEqualTo(gotUser);
    }

    @Test
    public void deleteNullUser() throws Exception {
        deleteAllData();

    }


    @Test
    public void updateUser() throws Exception {
        deleteAllData();
    }

    @Test
    public void updateNullUser() throws Exception {
        deleteAllData();
    }

    @Test
    public void getTableToView(){
        deleteAllData();
    }

    @Test
    public void getNullTableToView(){
        deleteAllData();
    }



    public void deleteAllData(){
        tableTestDao.get().deleteCharms();
        tableTestDao.get().deleteClientAccounts();
        tableTestDao.get().deleteClientAddrs();
        tableTestDao.get().deleteClients();
        tableTestDao.get().charmSerialToStart();
        tableTestDao.get().clientAccountSerialToStart();
        tableTestDao.get().clientSerialToStart();
    }


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
