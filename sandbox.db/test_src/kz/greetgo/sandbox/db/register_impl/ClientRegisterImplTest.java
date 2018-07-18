package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientAccount;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.db.classes.TestView;
import kz.greetgo.sandbox.db.helper.DateHelper;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

//FIXME имя теста должно начинаться с имени тестируемого метода

public class ClientRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientRegister> clientRegister;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> testDaoBeanGetter;

  public BeanGetter<ReportRegister> reportRegister;

  @Test
  public void checkActual() {
    testDaoBeanGetter.get().deleteAll();

    List<ClientRecord> notActual = getClientRecords(100);

    notActual.sort(Comparator.comparingDouble(c -> c.minBalance));

    testDaoBeanGetter.get().deleteAll();

    List<ClientRecord> actual = getClientRecords(100);

    actual.sort(Comparator.comparingDouble(c -> c.minBalance));


    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "min";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);


    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isNotEqualTo(notActual.get(i).id);
      assertThat(recordsFromDb.get(i).id).isEqualTo(actual.get(i).id);
    }
  }

  @Test
  public void checkCharactersNotNull() {
    testDaoBeanGetter.get().deleteAllCharms();
    List<String> charms = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      String charm = RND.str(5);
      testDaoBeanGetter.get().insertNewCharm(charm);
      charms.add(charm);
    }

    List<CharmRecord> charmRecords = clientRegister.get().charm();

    for (int i = 0; i < charmRecords.size(); i++) {
      assertThat(charmRecords.get(i).name).isEqualTo(charms.get(i));
    }

  }

  @Test
  public void deleteClient() {
    int id = insertNewClient();
    clientRegister.get().deleteClient(id);
    assertThat(testDaoBeanGetter.get().clientExists(id)).isNull();
  }

  @Test
  public void createNewClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = "CHECK12";
    clientToSave.surname = "CHECK12";
    clientToSave.patronymic = "CHECK12";
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();

    clientToSave.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientToSave.id = null;

    Phone phone = new Phone();
    phone.number = RND.str(11);
    phone.type = "MOBILE";
    phone.editedTo = phone.number;

    Address address = new Address();
    address.flat = "Flat";
    address.house = "House";
    address.street = "Street";
    address.type = "REG";

    clientToSave.addedPhones = new ArrayList<>();
    clientToSave.addedPhones.add(phone);

    clientToSave.addedAddresses = new ArrayList<>();
    clientToSave.addedAddresses.add(address);

    ClientRecord record = clientRegister.get().save(clientToSave);
    assertWithClientDot(testDaoBeanGetter.get().getClientDotById(record.id), record);
    assertWithAddressDot(testDaoBeanGetter.get().getAddressDot(record.id), address);
    assertWithPhoneDot(testDaoBeanGetter.get().getMobilePhone(record.id), phone);
  }

  @Test
  public void clientDetails() {
    int newClientId = insertNewClient();
    ClientDetails details = clientRegister.get().details(newClientId);

    ClientDot clientDot = testDaoBeanGetter.get().getClientDotById(newClientId);

    assertThat(clientDot.id).isEqualTo(details.id);
    assertThat(clientDot.name.equals(details.name));
    assertThat(clientDot.surname.equals(details.surname));
    assertThat(clientDot.patronymic.equals(details.patronymic));
    assertThat(clientDot.gender.equals(details.gender));

    List<AddressDot> addressDots = testDaoBeanGetter.get().getAddressDots(newClientId);

    assertThat(details.addresses).hasSameSizeAs(addressDots);

    for (int i = 0; i < details.addresses.size(); i++) {
      assertThat(details.addresses.get(i).street).isEqualTo(addressDots.get(i).street);
      assertThat(details.addresses.get(i).flat).isEqualTo(addressDots.get(i).flat);
      assertThat(details.addresses.get(i).house).isEqualTo(addressDots.get(i).house);
    }

    List<PhoneDot> phoneDots = testDaoBeanGetter.get().getPhoneDots(newClientId);

    assertThat(details.phones).hasSameSizeAs(phoneDots);

    for (int i = 0; i < details.phones.size(); i++) {
      assertThat(details.phones.get(i).number).isEqualTo(phoneDots.get(i).number);
      assertThat(details.phones.get(i).type).isEqualTo(phoneDots.get(i).type);
    }
  }

  @Test
  public void testCreateClientWithNotExistingGender() {

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "OTHER";
    clientToSave.birthDate = new Date();
    clientToSave.id = null;

    clientToSave.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(10));

    //
    //
    ClientRecord clientRecord = clientRegister.get().save(clientToSave);
    ClientDot dot = testDaoBeanGetter.get().getClientDotById(clientRecord.id);
    assertThat(dot.gender.equals("OTHER")).isTrue();
    //
    //
  }

  @Test
  public void testEditClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = "Neusus";
    clientToSave.surname = "Fiendirus";
    clientToSave.patronymic = "Torpall";
    clientToSave.gender = "FEMALE";
    clientToSave.birthDate = new Date();
    clientToSave.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientToSave.id = insertNewClient();

    Address editedAddress = new Address();
    editedAddress.clientId = clientToSave.id;
    editedAddress.flat = "Flat1";
    editedAddress.house = "House1";
    editedAddress.street = "Street1";
    editedAddress.type = "REG";

    clientToSave.editedAddresses = new ArrayList<>();

    clientToSave.editedAddresses.add(editedAddress);

    Phone phone = new Phone();
    phone.client_id = clientToSave.id;
    phone.number = testDaoBeanGetter.get().getMobilePhone(clientToSave.id).number;
    phone.editedTo = "777777777";
    phone.type = "MOBILE";

    clientToSave.editedPhones = new ArrayList<>();

    clientToSave.editedPhones.add(phone);

    ClientRecord clientRecord = clientRegister.get().save(clientToSave);

    assertWithClientDot(testDaoBeanGetter.get().getClientDotById(clientToSave.id), clientRecord);
    assertWithPhoneDot(testDaoBeanGetter.get().getMobilePhone(clientToSave.id), phone);
    assertWithAddressDot(testDaoBeanGetter.get().getRegAddress(clientToSave.id), editedAddress);
  }

  @Test
  public void testGetNotExistingClientDetails() {
    assertThat(testDaoBeanGetter.get().getClientDotById(-100)).isNull();
  }

  @Test
  public void testSortedByFIOAsc() {

    List<ClientDot> clientDots = getInsertedClients();

    clientDots.sort((o1, o2) ->
      Collator.getInstance(new Locale("ru", "RU")).compare(createFIO(o1), createFIO(o2)));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);

    assertThat(records).hasSameSizeAs(clientDots);

    for (int i = 0; i < records.size(); i++) {
      assertWithClientDot(clientDots.get(i), records.get(i));
    }


  }

  @Test
  public void testSortedByFIODesc() throws Exception {
    List<ClientDot> clientDots = getInsertedClients();
    clientDots.sort((o1, o2) ->
      Collator.getInstance(new Locale("ru", "RU")).compare(
        createFIO(o2), createFIO(o1)));


    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(records).hasSameSizeAs(clientDots);

    for (int i = 0; i < records.size(); i++) {
      assertWithClientDot(clientDots.get(i), records.get(i));
    }


    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientDots);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientDots.get(i).id);
      assertThat(testView.rows.get(i).name).isEqualTo(clientDots.get(i).name);
      assertThat(testView.rows.get(i).surname).isEqualTo(clientDots.get(i).surname);
      assertThat(testView.rows.get(i).patronymic).isEqualTo(clientDots.get(i).patronymic);
    }

    assertThat(testView.userName).isEqualTo("Test");
  }

  private String createFIO(ClientDot clientDot) {
    return clientDot.surname + clientDot.name + ((clientDot.patronymic != null) ? clientDot.patronymic : "");
  }

  @Test
  public void testSortedByMinAsc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> c.minBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "min";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).minBalance).isEqualTo(clientRecords.get(i).minBalance);
    }


    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).minBalance).isEqualTo(clientRecords.get(i).minBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");

  }

  @Test
  public void testSortedByMinDesc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> -c.minBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-min";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).minBalance).isEqualTo(clientRecords.get(i).minBalance);
    }


    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).minBalance).isEqualTo(clientRecords.get(i).minBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");

  }

  @Test
  public void testSortedByMaxAsc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> c.maxBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "max";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).maxBalance).isEqualTo(clientRecords.get(i).maxBalance);
    }

    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).maxBalance).isEqualTo(clientRecords.get(i).maxBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");
  }

  @Test
  public void testSortedByMaxDesc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> -c.maxBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-max";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).maxBalance).isEqualTo(clientRecords.get(i).maxBalance);
    }

    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).maxBalance).isEqualTo(clientRecords.get(i).maxBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");

  }

  @Test
  public void testSortedByTotalAsc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> c.accBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "total";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).accBalance).isEqualTo(clientRecords.get(i).accBalance);
    }

    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).accBalance).isEqualTo(clientRecords.get(i).accBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");
  }

  @Test
  public void testSortedByTotalDesc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecords(100);

    clientRecords.sort(Comparator.comparingDouble(c -> -c.accBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-total";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).accBalance).isEqualTo(clientRecords.get(i).accBalance);
    }


    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).accBalance).isEqualTo(clientRecords.get(i).accBalance);
    }

    assertThat(testView.userName).isEqualTo("Test");

  }

  @Test
  public void testSortedByAgeAsc() throws Exception {
    List<ClientRecord> clientRecords = getClientRecordsWithBirthDate(100);
    clientRecords.sort(Comparator.comparingInt(c -> c.age));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).age).isEqualTo(clientRecords.get(i).age);
    }


    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).age).isEqualTo(clientRecords.get(i).age);
    }

    assertThat(testView.userName).isEqualTo("Test");
  }

  @Test
  public void testSortedByAgeDesc() throws Exception {

    List<ClientRecord> clientRecords = getClientRecordsWithBirthDate(100);
    clientRecords.sort(Comparator.comparingInt(c -> -c.age));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDb = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDb).hasSize(10);

    for (int i = 0; i < recordsFromDb.size(); i++) {
      assertThat(recordsFromDb.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(recordsFromDb.get(i).age).isEqualTo(clientRecords.get(i).age);
    }

    TestView testView = new TestView();

    //
    //
    reportRegister.get().renderClientList(clientRecordFilter, "Test", testView);
    //
    //

    assertThat(testView.rows).hasSameSizeAs(clientRecords);

    for (int i = 0; i < testView.rows.size(); i++) {
      assertThat(testView.rows.get(i).id).isEqualTo(clientRecords.get(i).id);
      assertThat(testView.rows.get(i).age).isEqualTo(clientRecords.get(i).age);
    }

    assertThat(testView.userName).isEqualTo("Test");
  }


  @Test
  public void tooBigSliceNum() {
    clearAndInputClients();

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 1000000000;
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(10);
  }

  @Test//FIXME только один случай пагинации - нужно все
  public void paginationTest() {
    List<ClientRecord> clientRecords = getClientRecords(100);
    clientRecords.sort(Comparator.comparingDouble(c -> c.accBalance));

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "total";
    clientRecordFilter.paginationPage = 1;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> recordsFromDB = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    assertThat(recordsFromDB).hasSize(10);

    for (int i = 0; i < recordsFromDB.size(); i++) {
      assertThat(recordsFromDB.get(i).id).isEqualTo(clientRecords.get(i + 10).id);
    }
  }


  @Test
  public void clientCountIsValid() {
    clearAndInputClients();

    ClientRecordFilter filter = new ClientRecordFilter();
    filter.searchName = "";

    int clientCount = testDaoBeanGetter.get().getClientCount(filter);

    assertThat(clientCount).isEqualTo(10);
  }

  private List<ClientRecord> getClientRecordsWithBirthDate(int clientNum) {
    testDaoBeanGetter.get().deleteAll();

    List<ClientRecord> clientRecords = new ArrayList<>();

    for (int i = 0; i < clientNum; i++) {
      ClientRecord record = new ClientRecord();
      Date date = dateRandom(2000, 1980);
      record.id = insertClientWithDate(date);
      record.age = DateHelper.calculateAge(DateHelper.toLocalDate(date), DateHelper.toLocalDate(new Date()));
      clientRecords.add(record);
    }

    return clientRecords;
  }


  private int insertClientWithDate(Date date) {
    ClientDot clientDot = new ClientDot();
    clientDot.name = RND.str(10);
    clientDot.surname = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.gender = RND.str(4);
    clientDot.birthDate = date;
    clientDot.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    return clientDot.id = testDaoBeanGetter.get().insertNewClient(clientDot);
  }


  private Date dateRandom(int initialYear, int lastYear) {
    if (initialYear > lastYear) {
      int year = lastYear;
      lastYear = initialYear;
      initialYear = year;
    }

    Calendar cInitialYear = Calendar.getInstance();
    cInitialYear.set(Calendar.YEAR, initialYear);
    long offset = cInitialYear.getTimeInMillis();

    Calendar cLastYear = Calendar.getInstance();
    cLastYear.set(Calendar.YEAR, lastYear);
    long end = cLastYear.getTimeInMillis();

    long diff = end - offset + 1;
    Timestamp timestamp = new Timestamp(offset + (long) (Math.random() * diff));
    return new Date(timestamp.getTime());
  }

  private void clearAndInputClients() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }
  }

  public void assertWithClientDot(ClientDot clientDot, ClientRecord clientRecord) {

    assertThat(clientRecord.id).isEqualTo(clientDot.id);
    assertThat(clientRecord.name).isEqualTo(clientDot.name);
    assertThat(clientRecord.surname).isEqualTo(clientDot.surname);
    assertThat(clientRecord.patronymic).isEqualTo(clientDot.patronymic);
  }

  private void assertWithAddressDot(AddressDot addressDot, Address address) {
    assertThat(addressDot.flat.equals(address.flat)).isTrue();
    assertThat(addressDot.street.equals(address.street)).isTrue();
    assertThat(addressDot.house.equals(address.house)).isTrue();
  }

  private void assertWithPhoneDot(PhoneDot phoneDot, Phone phone) {
    assertThat(phoneDot.number).isEqualTo(phone.editedTo);
    assertThat(phoneDot.type).isEqualTo(phone.type);
  }

  private int insertNewClient() {
    ClientDot clientDot = new ClientDot();

    clientDot.name = RND.str(10);
    clientDot.surname = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.gender = RND.str(4);
    clientDot.birthDate = new Date();
    clientDot.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientDot.id = testDaoBeanGetter.get().insertNewClient(clientDot);

    AddressDot addressDot = new AddressDot();
    addressDot.client_id = clientDot.id;
    addressDot.flat = RND.str(10);
    addressDot.house = RND.str(10);
    addressDot.street = RND.str(10);
    addressDot.type = "REG";
    testDaoBeanGetter.get().insertNewAddressDot(addressDot);

    AddressDot fact = new AddressDot();
    fact.client_id = clientDot.id;
    fact.flat = RND.str(10);
    fact.house = RND.str(10);
    fact.street = RND.str(10);
    fact.type = "FACT";
    testDaoBeanGetter.get().insertNewAddressDot(fact);

    PhoneDot phoneDot = new PhoneDot();
    phoneDot.type = "MOBILE";
    phoneDot.number = RND.str(10);
    phoneDot.client_id = clientDot.id;
    testDaoBeanGetter.get().insertNewPhoneDot(phoneDot);

    PhoneDot phone2 = new PhoneDot();
    phone2.type = "WORK";
    phone2.number = RND.str(10);
    phone2.client_id = clientDot.id;
    testDaoBeanGetter.get().insertNewPhoneDot(phone2);

    return clientDot.id;
  }

  private List<Integer> insertClientsAndGetIds(int num) {
    testDaoBeanGetter.get().deleteAll();

    List<Integer> ids = new ArrayList<>();

    for (int i = 0; i < num; i++) {
      ids.add(insertNewClient());
    }
    return ids;
  }

  private List<ClientRecord> getClientRecords(int num) {
    List<Integer> ids = insertClientsAndGetIds(num);

    List<ClientRecord> clientRecords = new ArrayList<>();

    float money = 1;

    for (Integer id : ids) {
      testDaoBeanGetter.get().insertNewAccount(new ClientAccount(id, money));
      testDaoBeanGetter.get().insertNewAccount(new ClientAccount(id, money + 3));
      testDaoBeanGetter.get().insertNewAccount(new ClientAccount(id, money + 5));

      ClientRecord record = new ClientRecord();
      record.id = id;
      record.minBalance = money;
      record.accBalance = money + (money + 3) + (money + 5);
      record.maxBalance = money + 5;
      clientRecords.add(record);

      money++;
    }

    return clientRecords;
  }

  private List<ClientDot> getInsertedClients() {
    List<ClientDot> clientDotList = new ArrayList<>();
    testDaoBeanGetter.get().deleteAll();

    ClientDot clientDot2 = new ClientDot();
    clientDot2.name = "BBBB";
    clientDot2.surname = "BBBB";
    clientDot2.patronymic = "BBBB";
    clientDot2.gender = RND.str(4);
    clientDot2.birthDate = new Date();
    clientDot2.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientDot2.id = testDaoBeanGetter.get().insertNewClient(clientDot2);
    clientDotList.add(clientDot2);

    ClientDot clientDot1 = new ClientDot();
    clientDot1.name = "AAAA";
    clientDot1.surname = "AAAA";
    clientDot1.patronymic = "AAAA";
    clientDot1.gender = RND.str(4);
    clientDot1.birthDate = new Date();
    clientDot1.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientDot1.id = testDaoBeanGetter.get().insertNewClient(clientDot1);
    clientDotList.add(clientDot1);

    ClientDot clientDot4 = new ClientDot();
    clientDot4.name = "DDDD";
    clientDot4.surname = "DDDD";
    clientDot4.patronymic = "DDDD";
    clientDot4.gender = RND.str(4);
    clientDot4.birthDate = new Date();
    clientDot4.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientDot4.id = testDaoBeanGetter.get().insertNewClient(clientDot4);
    clientDotList.add(clientDot4);

    ClientDot clientDot3 = new ClientDot();
    clientDot3.name = "CCCC";
    clientDot3.surname = "CCCC";
    clientDot3.patronymic = "CCCC";
    clientDot3.gender = RND.str(4);
    clientDot3.birthDate = new Date();
    clientDot3.charm = testDaoBeanGetter.get().insertNewCharm(RND.str(5));
    clientDot3.id = testDaoBeanGetter.get().insertNewClient(clientDot3);
    clientDotList.add(clientDot3);

    return clientDotList;
  }

}
