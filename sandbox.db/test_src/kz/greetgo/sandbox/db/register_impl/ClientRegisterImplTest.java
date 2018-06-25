package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.Collator;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientRegister> clientRegister;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> testDaoBeanGetter;

  @Test
  public void checkCharactersNotNull() {
    List<CharmRecord> charmRecords = clientRegister.get().charm();

    for (CharmRecord charmRecord : charmRecords) {
      assertThat(charmRecord.name == null || charmRecord.name.length() == 0).isFalse();
    }
  }

  @Test
  public void deleteClient() {
    int id = testDaoBeanGetter.get().getFirstClient();
    clientRegister.get().deleteClient(id);
    assertThat(testDaoBeanGetter.get().clientExists(id)).isNull();
  }

  @Test
  public void createNewClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();

    clientToSave.charm = testDaoBeanGetter.get().insertNewCharacter(RND.str(5));
    clientToSave.id = null;

    Phone phone = new Phone();
    phone.number = RND.str(11);
    phone.type = "MOBILE";

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

    compareWithClientDot(record.id, record);
    compareWithAddressDot(testDaoBeanGetter.get().getAddressDot(record.id), address);
    compareWithPhoneDot(testDaoBeanGetter.get().getPhoneDot(record.id), phone);
  }

  private void compareWithPhoneDot(PhoneDot phoneDot, Phone phone) {
    assertThat(phoneDot.number.equals(phone.number)).isTrue();
    assertThat(phoneDot.type.equals(phone.type)).isTrue();
  }

  public void compareWithClientDot(int clientId, ClientRecord clientRecord) {

    ClientRecord clientRecordFromTest = testDaoBeanGetter.get().getClientRecordById(clientId);
    assertThat(clientRecord.name.equals(clientRecordFromTest.name)).isTrue();
    assertThat(clientRecord.surname.equals(clientRecordFromTest.surname)).isTrue();
    assertThat(clientRecord.patronymic.equals(clientRecordFromTest.patronymic)).isTrue();
    assertThat(clientRecord.charm.equals(clientRecordFromTest.charm)).isTrue();
    assertThat(clientRecord.maxBalance == clientRecordFromTest.maxBalance).isTrue();
    assertThat(clientRecord.minBalance == clientRecordFromTest.minBalance).isTrue();
    assertThat(clientRecord.accBalance == clientRecordFromTest.accBalance).isTrue();
  }

  private void compareWithAddressDot(AddressDot addressDot, Address address) {
    assertThat(addressDot.flat.equals(address.flat)).isTrue();
    assertThat(addressDot.street.equals(address.street)).isTrue();
    assertThat(addressDot.house.equals(address.house)).isTrue();
  }


  private int insertNewClient() {
    testDaoBeanGetter.get().insertNewCharacter(RND.str(5));
    ClientDot clientDot = new ClientDot();

    clientDot.name = RND.str(10);
    clientDot.surname = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.gender = RND.str(4);
    clientDot.birthDate = new Date();
    clientDot.charm = 1;
    clientDot.id = testDaoBeanGetter.get().insertNewClient(clientDot);

    AddressDot addressDot = new AddressDot();
    addressDot.client_id = clientDot.id;
    addressDot.flat = RND.str(10);
    addressDot.house = RND.str(10);
    addressDot.street = RND.str(10);
    addressDot.type = "REG";
    testDaoBeanGetter.get().insertNewAddressDot(addressDot);

    PhoneDot phoneDot = new PhoneDot();
    phoneDot.type = "MOBILE";
    phoneDot.number = RND.str(10);
    phoneDot.client_id = clientDot.id;
    testDaoBeanGetter.get().insertNewPhoneDot(phoneDot);

    return clientDot.id;
  }

  @Test
  public void clientDetails() {
    int newClientId = insertNewClient();
    ClientDetails details = clientRegister.get().details(newClientId);

    ClientDot clientDot = testDaoBeanGetter.get().getClientDotById(newClientId);
    assertThat(clientDot.name.equals(details.name));
    assertThat(clientDot.surname.equals(details.surname));
    assertThat(clientDot.patronymic.equals(details.patronymic));
    assertThat(clientDot.gender.equals(details.gender));
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

    clientToSave.charm = 1;

    //
    //
    ClientRecord clientRecord = clientRegister.get().save(clientToSave);
    ClientDot dot = testDaoBeanGetter.get().getClientDotById(clientRecord.id);
    assertThat(dot.gender.equals("OTHER")).isTrue();
    //
    //
  }

  @Test
  public void testCreateClientWithNotExistingCharacter() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.id = null;

    clientToSave.charm = -1;

    //
    //
    clientRegister.get().save(clientToSave);
    assertThat(testDaoBeanGetter.get().getClientDotWithCharmId(-1) == null).isTrue();
    //
    //
  }

  @Test
  public void testEditClient() {

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = "Neus";
    clientToSave.surname = "Fiendir";
    clientToSave.patronymic = "Torpa";
    clientToSave.gender = "FEMALE";
    clientToSave.birthDate = new Date();
    clientToSave.charm = testDaoBeanGetter.get().insertNewCharacter(RND.str(5));
    clientToSave.id = insertNewClient();

    Address editedAddress = new Address();
    editedAddress.clientId = clientToSave.id;
    editedAddress.flat = "Flat1";
    editedAddress.house = "House1";
    editedAddress.street = "Street1";
    editedAddress.type = "REG";
    clientToSave.editedAddresses = new ArrayList<>();
    clientToSave.editedAddresses.add(editedAddress);

    ClientRecord clientRecord = clientRegister.get().save(clientToSave);


    AddressDot addressDot = new AddressDot();
    addressDot.flat = "Flat1";
    addressDot.house = "House1";
    addressDot.street = "Street1";
    addressDot.type = "REG";

    compareWithClientDot(clientToSave.id, clientRecord);
    compareWithAddressDot(testDaoBeanGetter.get().getAddressDot(clientRecord.id), editedAddress);
  }

  @Test
  public void testGetNotExistingClientDetails() {
    assertThat(testDaoBeanGetter.get().getClientDotById(-100) == null &&
      clientRegister.get().details(-100) == null).isTrue();
  }

  @Test
  public void searchForEmptyName() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = "";

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);

    List<ClientDot> dots = testDaoBeanGetter.get().getClientDotsWithFIO("");

    assertThat(records.size() == dots.size()).isTrue();

    for (int i = 0; i < records.size(); i++) {
      assertThat(records.get(i).name.equals(dots.get(i).name));
      assertThat(records.get(i).surname.equals(dots.get(i).name));
      assertThat(records.get(i).patronymic.equals(dots.get(i).name));
    }

  }

  @Test
  public void testSortedByFIONameAsc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);

    Comparator<ClientRecord> clientRecordFIOComparator = (o1, o2) ->
      Collator.getInstance(new Locale("ru", "RU")).compare(o2.surname + o2.name + o2.patronymic, o1.surname + o1.name + o1.patronymic);

    isSorted(clientRecordFIOComparator, records);
  }

  @Test
  public void testSortedByFIONameDesc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordFIOComparator = (o1, o2) ->
      -Collator.getInstance(new Locale("ru", "RU")).compare(o2.surname + o2.name + o2.patronymic, o1.surname + o1.name + o1.patronymic);


    isSorted(clientRecordFIOComparator, records);

  }

  @Test
  public void testSortedByMinAsc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "min";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordMinComparator = (o1, o2) -> o1.minBalance >= o2.minBalance ? 1 : -1;


    isSorted(clientRecordMinComparator, records);

  }

  @Test
  public void testSortedByMinDesc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-min";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordMinComparator = (o1, o2) -> o1.minBalance <= o2.minBalance ? 1 : -1;


    isSorted(clientRecordMinComparator, records);

  }

  @Test
  public void testSortedByMaxAsc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "max";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordMaxComparator = (o1, o2) -> o1.maxBalance >= o2.maxBalance ? 1 : -1;


    isSorted(clientRecordMaxComparator, records);

  }

  @Test
  public void testSortedByMaxDesc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-max";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordMaxComparator = (o1, o2) -> o1.maxBalance <= o2.maxBalance ? 1 : -1;


    isSorted(clientRecordMaxComparator, records);

  }

  @Test
  public void testSortedByTotalAsc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "total";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordTotalComparator = (o1, o2) -> o1.accBalance >= o2.accBalance ? 1 : -1;


    isSorted(clientRecordTotalComparator, records);

  }


  @Test
  public void testSortedByTotalDesc() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-total";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordTotalComparator = (o1, o2) -> o1.accBalance <= o2.accBalance ? 1 : -1;


    isSorted(clientRecordTotalComparator, records);

  }

  //BeforeMethod, dataProvider
  @Test
  public void testSortedByAgeAsc() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordAgeComparator = (o1, o2) -> o1.age >= o2.age ? 1 : -1;


    isSorted(clientRecordAgeComparator, records);

  }

  @Test
  public void testSortedByAgeDesc() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //

    Comparator<ClientRecord> clientRecordAgeComparator = (o1, o2) -> o1.age <= o2.age ? 1 : -1;

    isSorted(clientRecordAgeComparator, records);

  }

  private void isSorted(Comparator<ClientRecord> comparator, List<ClientRecord> records) {
    ClientRecord previous = null;

    for (ClientRecord clientRecord : records) {
      if (previous != null) {
        assertThat(comparator.compare(previous, clientRecord) < 0).isFalse();
      }
      previous = clientRecord;
    }
  }

  @Test
  public void testInvalidSliceNum() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }


    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = -1;
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(1);
  }

  @Test
  public void tooBigSliceNum() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 1000000000;
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(20);
  }

  @Test
  public void invalidPaginationPage() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 100; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = -1;
    clientRecordFilter.sliceNum = 10;

    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(0);
  }

  @Test
  public void getNonExistingPaginationPage() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 100; i++) {
      insertNewClient();
    }

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = -1;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = null;

    //
    //
    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(0);
    //
    //
  }

  @Test
  public void clientCountIsValid() {
    testDaoBeanGetter.get().deleteAll();
    for (int i = 0; i < 100; i++) {
      insertNewClient();
    }
    ClientRecordFilter filter = new ClientRecordFilter();
    filter.searchName = "";

    int clientCount = testDaoBeanGetter.get().getClientCount(filter);

    int clientCountFromImpl = clientRegister.get().getClientCount(filter);

    assertThat(clientCount == clientCountFromImpl).isTrue();
  }

}
