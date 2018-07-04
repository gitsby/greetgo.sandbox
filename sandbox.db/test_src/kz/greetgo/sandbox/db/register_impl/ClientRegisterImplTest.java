package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Address;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientRegister> clientRegister;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> testDaoBeanGetter;

  @Test
  public void checkCharactersNotNull() {
    testDaoBeanGetter.get().deleteAllCharms();
    List<String> charms = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      String charm = RND.str(5);
      testDaoBeanGetter.get().insertNewCharacter(charm);
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

    assertWithClientDot(testDaoBeanGetter.get().getClientDotById(record.id), record);
    assertWithAddressDot(testDaoBeanGetter.get().getAddressDot(record.id), address);
    compareWithPhoneDot(testDaoBeanGetter.get().getPhoneDot(record.id), phone);
  }

  private void compareWithPhoneDot(PhoneDot phoneDot, Phone phone) {
    assertThat(phoneDot.number.equals(phone.number)).isTrue();
    assertThat(phoneDot.type.equals(phone.type)).isTrue();
  }

  public void assertWithClientDot(ClientDot clientDot, ClientRecord clientRecord) {
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
    assertThat(phoneDot.client_id).isEqualTo(phone.client_id);
    assertThat(phoneDot.number).isEqualTo(phone.editedTo);
    assertThat(phoneDot.type).isEqualTo(phone.type);
  }

  private int insertNewClient() {
    testDaoBeanGetter.get().insertNewCharacter(RND.str(5));
    ClientDot clientDot = new ClientDot();

    clientDot.name = RND.str(10);
    clientDot.surname = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.gender = RND.str(4);
    clientDot.birthDate = new Date();
    clientDot.charm = testDaoBeanGetter.get().insertNewCharacter(RND.str(5));
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

    assertThat(clientDot.id).isEqualTo(details.id);
    assertThat(clientDot.name.equals(details.name));
    assertThat(clientDot.surname.equals(details.surname));
    assertThat(clientDot.patronymic.equals(details.patronymic));
    assertThat(clientDot.gender.equals(details.gender));

    List<AddressDot> addressDots = testDaoBeanGetter.get().getAddressDots(newClientId);

    for (int i = 0; i < details.addresses.size(); i++) {
      assertThat(details.addresses.get(i).street).isEqualTo(addressDots.get(i).street);
      assertThat(details.addresses.get(i).flat).isEqualTo(addressDots.get(i).flat);
      assertThat(details.addresses.get(i).house).isEqualTo(addressDots.get(i).house);
    }

    List<PhoneDot> phoneDots = testDaoBeanGetter.get().getPhoneDots(newClientId);
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

    clientToSave.charm = testDaoBeanGetter.get().insertNewCharacter(RND.str(10));

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

    Phone phone = new Phone();
    phone.client_id = clientToSave.id;
    phone.number = testDaoBeanGetter.get().getPhoneDot(clientToSave.id).number;
    phone.editedTo = "777777777";
    phone.type = "MOBILE";

    clientToSave.editedPhones = new ArrayList<>();
    clientToSave.editedPhones.add(phone);

    ClientRecord clientRecord = clientRegister.get().save(clientToSave);

    PhoneDot phoneDot = new PhoneDot();
    phoneDot.client_id = clientToSave.id;
    phoneDot.number = "777777777";
    phoneDot.type = "MOBILE";

    assertWithClientDot(testDaoBeanGetter.get().getClientDotById(clientToSave.id), clientRecord);
    assertWithPhoneDot(testDaoBeanGetter.get().getPhoneDot(clientToSave.id), phone);
    assertWithAddressDot(testDaoBeanGetter.get().getAddressDot(clientRecord.id), editedAddress);
  }

  @Test
  public void testGetNotExistingClientDetails() {
    assertThat(testDaoBeanGetter.get().getClientDotById(-100)).isNull();
  }

  @Test
  public void testSortedByFIONameAsc() {
    clearAndInputClients();

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);

    Comparator<ClientRecord> clientRecordFIOComparator = (o1, o2) ->
      Collator.getInstance(new Locale("ru", "RU")).compare(o2.surname + o2.name + o2.patronymic, o1.surname + o1.name + o1.patronymic);

    isSorted(clientRecordFIOComparator, records);
  }

  private void clearAndInputClients() {
    testDaoBeanGetter.get().deleteAll();

    for (int i = 0; i < 10; i++) {
      insertNewClient();
    }
  }

  @Test
  public void testSortedByFIONameDesc() {
    clearAndInputClients();


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

    // FIXME: 7/4/18 = Comparator.comparingDouble(o -> o.minBalance)
    Comparator<ClientRecord> clientRecordMinComparator = (o1, o2) -> o1.minBalance >= o2.minBalance ? 1 : -1;


    isSorted(clientRecordMinComparator, records);

  }

  @Test
  public void testSortedByMinDesc() {
    clearAndInputClients();

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
    clearAndInputClients();

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
    clearAndInputClients();

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
    clearAndInputClients();

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
    clearAndInputClients();

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

  @Test
  public void testSortedByAgeAsc() {
    clearAndInputClients();

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
    clearAndInputClients();

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    //
    //
    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);
    //
    //


    // FIXME: 7/4/18 Тест не правильный. Везде должны быть ассерты(хотябы по айди)

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
  public void tooBigSliceNum() {
    clearAndInputClients();

    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 1000000000;
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter)).hasSize(10);
  }

  @Test
  public void clientCountIsValid() {

    clearAndInputClients();

    ClientRecordFilter filter = new ClientRecordFilter();
    filter.searchName = "";

    int clientCount = testDaoBeanGetter.get().getClientCount(filter);

    assertThat(clientCount).isEqualTo(10);
  }

}
