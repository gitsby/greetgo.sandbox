package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigration;
import kz.greetgo.sandbox.db.test.dao.CIAMigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class CIAInMigrationTest extends ParentTestNg {

  CIAInMigration inMigration = new CIAInMigration();

  public BeanGetter<CIAMigrationTestDao> ciaMigrationDao;

  @BeforeMethod
  public void createTables() throws Exception {
    inMigration.connect();
    inMigration.prepareWorker();

    ciaMigrationDao.get().createTempClientTable();
    ciaMigrationDao.get().createTempAddressTable();
    ciaMigrationDao.get().createTempPhoneTable();
  }

  @AfterMethod
  public void dropTables() throws SQLException {

    ciaMigrationDao.get().dropTempClientTable();
    ciaMigrationDao.get().dropTempAddressTable();
    ciaMigrationDao.get().dropTempPhoneTable();

    inMigration.closeConnection();
  }

  @Test
  public void testInsertClientIntoTemp() throws SQLException {

    List<ClientFromMigration> clients = getRandomClients(30);

    inMigration.sendClient(clients);

    List<ClientFromMigration> clientFromMigrations = ciaMigrationDao.get().getTempClients();

    assertThat(clientFromMigrations).hasSize(30);
    assertClients(clients, clientFromMigrations);


    for (ClientFromMigration client : clients) {
      client.name = RND.str(10);
      client.surname = RND.str(10);
      client.patronymic = RND.str(10);
      client.gender = RND.str(4);
      client.birth = "1980-09-09";
      client.charm = RND.str(10);
      client.error = new StringBuilder();
      client.timestamp = new Timestamp(new Date().getTime());
    }

    //
    //
    inMigration.sendClient(clients);
    //
    //


    clientFromMigrations = ciaMigrationDao.get().getTempClients();


    assertThat(clientFromMigrations).hasSize(30);
    assertClients(clients, clientFromMigrations);
  }

  @Test
  public void testInsertPhoneIntoTemp() throws SQLException {
    List<PhoneFromMigration> phones = getRandomPhones(30);

    //
    //
    inMigration.sendPhones(phones);
    //
    //

    List<PhoneFromMigration> phoneFromMigrations = ciaMigrationDao.get().getTempPhones();

    assertThat(phoneFromMigrations).hasSize(30);
    assertPhones(phones, phoneFromMigrations);
  }


  @Test
  public void testInsertAddressIntoTemp() throws SQLException {
    List<AddressFromMigration> addresses = getRandomAddresses(30);

    //
    //
    inMigration.sendAddresses(addresses);
    //
    //

    List<AddressFromMigration> addressFromMigration = ciaMigrationDao.get().getAddresses();

    assertThat(addressFromMigration).hasSize(30);
    assertAddresses(addresses, addressFromMigration);

    for (AddressFromMigration address : addresses) {
      address.flat = RND.str(10);
      address.house = RND.str(10);
      address.street = RND.str(10);
    }

    //
    //
    inMigration.sendAddresses(addresses);
    //
    //

    addressFromMigration = ciaMigrationDao.get().getAddresses();


    assertThat(addressFromMigration).hasSize(30);
    assertAddresses(addresses, addressFromMigration);
  }


  @Test
  public void testInsertClientIntoReal() throws SQLException {
    List<ClientFromMigration> clients = getRandomClients(30);

    inMigration.sendClient(clients);
    inMigration.insertTempClientsToReal();


  }

  @Test
  public void testInsertPhoneIntoReal() {


  }

  @Test
  public void testInsertAddressIntoReal() {

  }

  private void assertAddresses(List<AddressFromMigration> addresses, List<AddressFromMigration> addressFromMigration) {
    for (int i = 0; i < addressFromMigration.size(); i++) {
      assertThat(addressFromMigration.get(i).client_id).isEqualTo(addresses.get(i).client_id);
      assertThat(addressFromMigration.get(i).flat).isEqualTo(addresses.get(i).flat);
      assertThat(addressFromMigration.get(i).house).isEqualTo(addresses.get(i).house);
      assertThat(addressFromMigration.get(i).street).isEqualTo(addresses.get(i).street);
      assertThat(addressFromMigration.get(i).type).isEqualTo(addresses.get(i).type);
    }
  }

  private List<AddressFromMigration> getRandomAddresses(int num) {
    List<AddressFromMigration> addresses = new ArrayList<>();

    for (int i = 0; i < num; i++) {
      addresses.add(createAddres());
    }
    return addresses;
  }

  private void assertPhones(List<PhoneFromMigration> phones, List<PhoneFromMigration> phoneFromMigrations) {
    for (int i = 0; i < phones.size(); i++) {
      assertThat(phoneFromMigrations.get(i).client_id).isEqualTo(phones.get(i).client_id);
      assertThat(phoneFromMigrations.get(i).number).isEqualTo(phones.get(i).number);
      assertThat(phoneFromMigrations.get(i).type).isEqualTo(phones.get(i).type);
    }
  }

  private void assertClients(List<ClientFromMigration> clients1, List<ClientFromMigration> clients2) {
    for (int i = 0; i < clients1.size(); i++) {
      assertThat(clients1.get(i).client_id).isEqualTo(clients2.get(i).client_id);
      assertThat(clients1.get(i).name).isEqualTo(clients2.get(i).name);
      assertThat(clients1.get(i).surname).isEqualTo(clients2.get(i).surname);
      assertThat(clients1.get(i).patronymic).isEqualTo(clients2.get(i).patronymic);
    }
  }

  private List<PhoneFromMigration> getRandomPhones(int num) {
    List<PhoneFromMigration> phones = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      phones.add(createPhone());
    }
    return phones;
  }

  private List<ClientFromMigration> getRandomClients(int num) {
    List<ClientFromMigration> clients = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      clients.add(createClient());
    }
    return clients;
  }

  private ClientFromMigration createClient() {
    ClientFromMigration client = new ClientFromMigration();
    client.client_id = RND.str(10);
    client.name = RND.str(10);
    client.surname = RND.str(10);
    client.patronymic = RND.str(10);
    client.gender = RND.str(4);
    client.birth = "1980-09-09";
    client.charm = RND.str(10);
    client.error = new StringBuilder();
    client.timestamp = new Timestamp(new Date().getTime());
    return client;
  }

  private AddressFromMigration createAddres() {
    AddressFromMigration addressFromMigration = new AddressFromMigration();
    addressFromMigration.client_id = RND.str(10);
    addressFromMigration.flat = RND.str(10);
    addressFromMigration.house = RND.str(10);
    addressFromMigration.street = RND.str(10);
    addressFromMigration.type = "REG";
    return addressFromMigration;
  }

  private PhoneFromMigration createPhone() {
    PhoneFromMigration phone = new PhoneFromMigration();
    phone.client_id = RND.str(10);
    phone.number = RND.str(10);
    phone.type = "MOBILE";
    return phone;
  }

}
