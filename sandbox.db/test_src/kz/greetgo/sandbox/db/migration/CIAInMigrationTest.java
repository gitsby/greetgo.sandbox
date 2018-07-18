package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import kz.greetgo.sandbox.db.migration.reader.xml.XMLManager;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigration;
import kz.greetgo.sandbox.db.stand.model.AddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import kz.greetgo.sandbox.db.test.dao.CIAMigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;

public class CIAInMigrationTest extends ParentTestNg {

  public BeanGetter<DbConfig> dbConfig;
  public BeanGetter<CIAMigrationTestDao> ciaMigrationDao;

  CIAInMigration inMigration;

  XMLManager xmlManager;

  @BeforeMethod
  public void createTables() throws Exception {
    inMigration =new CIAInMigration(connectToDatabase());
    inMigration.prepareWorker();

    dropAllTables();

    ciaMigrationDao.get().createTempClientTable();
    ciaMigrationDao.get().createTempAddressTable();
    ciaMigrationDao.get().createTempPhoneTable();
  }

  private Connection connectToDatabase() throws SQLException {
    String url = dbConfig.get().url();
    Properties properties = new Properties();
    properties.setProperty("user", dbConfig.get().username());
    properties.setProperty("password", dbConfig.get().password());
    return DriverManager.getConnection(url, properties);
  }

  @AfterMethod
  public void resetDb() throws SQLException {

    dropAllTables();
    inMigration.closeConnection();
  }

  private void dropAllTables() {
    ciaMigrationDao.get().deleteFromCharms();
    ciaMigrationDao.get().deleteFromClient();
    ciaMigrationDao.get().dropTempClientTable();
    ciaMigrationDao.get().dropTempAddressTable();
    ciaMigrationDao.get().dropTempPhoneTable();
  }

  @Test
  public void testInsertClientIntoTemp() throws FileNotFoundException, UnsupportedEncodingException {

    List<ClientFromMigration> clients = createClientXmlFile();

    //
    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> inMigration.sendClient(client), address -> {
    }, phonesFromMigration -> {
    });
    //
    //

    while (ciaMigrationDao.get().getTempClients().size() == 0) ;

    List<ClientFromMigration> clientFromMigrations = ciaMigrationDao.get().getTempClients();

    assertThat(clientFromMigrations).hasSize(3);
    assertClients(clients, clientFromMigrations);
  }

  @Test
  public void testInsertPhoneIntoTemp() throws FileNotFoundException, UnsupportedEncodingException {
    List<PhoneFromMigration> phones = createPhones();

    //
    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> {
    }, address -> {
    }, phonesFromMigration -> inMigration.sendPhones(phonesFromMigration));
    //
    //

    while (ciaMigrationDao.get().getTempPhones().size() == 0) ;

    List<PhoneFromMigration> phoneFromMigrations = ciaMigrationDao.get().getTempPhones();

    assertThat(phoneFromMigrations).hasSameSizeAs(phones);
    assertPhones(phones, phoneFromMigrations);
  }


  @Test
  public void testInsertAddressIntoTemp() throws FileNotFoundException, UnsupportedEncodingException {
    List<AddressFromMigration> addresses = createAddressXml();

    //
    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> {
    }, address -> inMigration.sendAddresses(address), phonesFromMigration -> {
    });
    //
    //
    while (ciaMigrationDao.get().getTempAddresses().size() == 0) ;

    List<AddressFromMigration> addressFromMigration = ciaMigrationDao.get().getTempAddresses();

    assertThat(addressFromMigration).hasSameSizeAs(addresses);
    assertAddresses(addresses, addressFromMigration);
  }


  @Test
  public void testInsertClientIntoReal() throws SQLException, FileNotFoundException, UnsupportedEncodingException {
    List<ClientFromMigration> clients = createClientXmlFile();

    //
    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> inMigration.sendClient(client), address -> {
    }, phonesFromMigration -> {
    });
    //
    //

    while (ciaMigrationDao.get().getTempClients().size() == 0) ;

    inMigration.insertTempClientsToReal();

    List<ClientDot> clientDots = ciaMigrationDao.get().getClientDots();

    assertThat(clientDots).hasSize(2);
    for (int i = 0; i < clientDots.size(); i++) {
      assertThat(clientDots.get(i).name).isEqualTo(clients.get(i).name);
      assertThat(clientDots.get(i).surname).isEqualTo(clients.get(i).surname);
      assertThat(clientDots.get(i).patronymic).isEqualTo(clients.get(i).patronymic);
    }

  }

  @Test
  public void testInsertPhoneIntoReal() throws FileNotFoundException, UnsupportedEncodingException, SQLException {
    insertClientIntoTemp();

    List<PhoneFromMigration> phones = createPhones();

    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> {
    }, address -> {
    }, phonesFromMigration -> inMigration.sendPhones(phonesFromMigration));
    //
    //

    while (ciaMigrationDao.get().getTempPhones().size() == 0) ;

    inMigration.insertTempClientsToReal();
    inMigration.insertTempPhone();


    List<PhoneDot> phoneDots = ciaMigrationDao.get().getPhonesFromReal();

    assertThat(phoneDots).hasSameSizeAs(phones);
    for (int i = 0; i < phoneDots.size(); i++) {
      assertThat(phoneDots.get(i).number).isEqualTo(phones.get(i).number);
      assertThat(phoneDots.get(i).type).isEqualTo(phones.get(i).type);
    }

  }


  @Test
  public void testInsertAddressIntoReal() throws FileNotFoundException, UnsupportedEncodingException, SQLException {
    insertClientIntoTemp();

    List<AddressFromMigration> addresses = createAddressXml();

    //
    //
    xmlManager = new XMLManager("build/test_cia.xml");
    xmlManager.load(client -> {
    }, address -> inMigration.sendAddresses(address), phonesFromMigration -> {
    });
    //
    //

    while (ciaMigrationDao.get().getTempAddresses().size() == 0) ;

    inMigration.insertTempClientsToReal();
    inMigration.insertTempAddressToReal();

    List<AddressDot> addressDots = ciaMigrationDao.get().getAddressDots();

    assertThat(addressDots).hasSize(4);

    for (int i = 0; i < addressDots.size(); i++) {
      assertThat(addressDots.get(i).flat).isEqualTo(addresses.get(i).flat);
      assertThat(addressDots.get(i).house).isEqualTo(addresses.get(i).house);
      assertThat(addressDots.get(i).street).isEqualTo(addresses.get(i).street);
      assertThat(addressDots.get(i).type).isEqualTo(addresses.get(i).type);
    }
  }

  private void insertClientIntoTemp() {
    ClientFromMigration client = createClient();
    client.client_id = "1";

    ciaMigrationDao.get().insertTempClient(client);

    ClientFromMigration client2 = createClient();
    client2.client_id = "2";

    ciaMigrationDao.get().insertTempClient(client2);
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

  private void assertPhones(List<PhoneFromMigration> phones, List<PhoneFromMigration> phoneFromMigrations) {
    for (int i = 0; i < phones.size(); i++) {
      assertThat(phoneFromMigrations.get(i).client_id).isEqualTo(phones.get(i).client_id);
      assertThat(phoneFromMigrations.get(i).number).isEqualTo(phones.get(i).number);
      assertThat(phoneFromMigrations.get(i).type).isEqualTo(phones.get(i).type);
    }
  }

  private List<ClientFromMigration> createClientXmlFile() throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("build/test_cia.xml", "UTF-8");
    writer.println("<cia>");
    List<ClientFromMigration> clients = new ArrayList<>();

    ClientFromMigration client1 = new ClientFromMigration();
    client1.client_id = "1";
    client1.surname = RND.str(10);
    client1.name = RND.str(10);
    client1.patronymic = RND.str(10);
    client1.gender = RND.str(10);
    client1.birth = "1980-02-02";
    client1.error = new StringBuilder();
    client1.charm = RND.str(10);
    client1.timestamp = new Timestamp(new Date().getTime());
    writer.println(toClientXML(client1));
    clients.add(client1);

    ClientFromMigration client2 = new ClientFromMigration();
    client2.client_id = "2";
    client2.surname = RND.str(10);
    client2.name = RND.str(10);
    client2.patronymic = RND.str(10);
    client2.gender = RND.str(10);
    client2.birth = "1912-02-02";
    client2.error = new StringBuilder();
    client2.charm = RND.str(10);
    client2.timestamp = new Timestamp(new Date().getTime());
    writer.println(toClientXML(client2));

    ClientFromMigration client3 = new ClientFromMigration();
    client3.client_id = "2";
    client3.surname = RND.str(10);
    client3.name = RND.str(10);
    client3.patronymic = RND.str(10);
    client3.gender = RND.str(10);
    client3.birth = "1980-02-02";
    client3.error = new StringBuilder();
    client3.charm = RND.str(10);
    client3.timestamp = new Timestamp(new Date().getTime());
    writer.println(toClientXML(client3));
    clients.add(client3);

    ClientFromMigration client4 = new ClientFromMigration();
    client4.client_id = "3";
    client4.surname = "";
    client4.name = "";
    client4.patronymic = "";
    client4.gender = "";
    client4.birth = "1980-02-02";
    client4.error = new StringBuilder();
    client4.charm = RND.str(10);
    client4.timestamp = new Timestamp(new Date().getTime());
    writer.println(toClientXML(client4));
    clients.add(client4);

    writer.println("\n</cia>");
    writer.close();
    return clients;
  }


  private List<AddressFromMigration> createAddressXml() throws FileNotFoundException, UnsupportedEncodingException {
    List<AddressFromMigration> addresses = new ArrayList<>();
    PrintWriter writer = new PrintWriter("build/test_cia.xml", "UTF-8");
    writer.println("<cia>");
    AddressFromMigration reg = new AddressFromMigration();
    reg.client_id = "1";
    reg.flat = RND.str(3);
    reg.street = RND.str(3);
    reg.house = RND.str(3);
    reg.type = "REG";

    AddressFromMigration fact = new AddressFromMigration();
    fact.client_id = "1";
    fact.flat = RND.str(3);
    fact.street = RND.str(3);
    fact.house = RND.str(3);
    fact.type = "FACT";
    writer.println(toAddressXML(reg, fact));

    AddressFromMigration reg2 = new AddressFromMigration();
    reg2.client_id = "2";
    reg2.flat = RND.str(3);
    reg2.street = RND.str(3);
    reg2.house = RND.str(3);
    reg2.type = "REG";

    AddressFromMigration fact2 = new AddressFromMigration();
    fact2.client_id = "2";
    fact2.flat = RND.str(3);
    fact2.street = RND.str(3);
    fact2.house = RND.str(3);
    fact2.type = "FACT";
    writer.println(toAddressXML(reg2, fact2));

    AddressFromMigration reg3 = new AddressFromMigration();
    reg3.client_id = "1";
    reg3.flat = RND.str(3);
    reg3.street = RND.str(3);
    reg3.house = RND.str(3);
    reg3.type = "REG";

    AddressFromMigration fact3 = new AddressFromMigration();
    fact3.client_id = "1";
    fact3.flat = RND.str(3);
    fact3.street = RND.str(3);
    fact3.house = RND.str(3);
    fact3.type = "FACT";
    writer.println(toAddressXML(reg3, fact3));

    addresses.add(fact3);
    addresses.add(reg3);

    addresses.add(fact2);
    addresses.add(reg2);

    writer.println("\n</cia>");
    writer.close();
    return addresses;
  }

  private List<PhoneFromMigration> createPhones() throws FileNotFoundException, UnsupportedEncodingException {
    List<PhoneFromMigration> phones = new ArrayList<>();
    PrintWriter writer = new PrintWriter("build/test_cia.xml", "UTF-8");
    writer.println("<cia>");

    PhoneFromMigration mobilePhone = new PhoneFromMigration();
    mobilePhone.client_id = "1";
    mobilePhone.number = RND.str(10);
    mobilePhone.type = "MOBILE";

    PhoneFromMigration homePhone = new PhoneFromMigration();
    homePhone.client_id = "1";
    homePhone.number = RND.str(10);
    homePhone.type = "HOME";
    phones.add(mobilePhone);
    phones.add(homePhone);
    writer.println(toPhoneXML(mobilePhone, homePhone));


    PhoneFromMigration mobilePhone1 = new PhoneFromMigration();
    mobilePhone1.client_id = "2";
    mobilePhone1.number = RND.str(10);
    mobilePhone1.type = "MOBILE";

    PhoneFromMigration homePhone1 = new PhoneFromMigration();
    homePhone1.client_id = "2";
    homePhone1.number = RND.str(10);
    homePhone1.type = "HOME";
    phones.add(mobilePhone1);
    phones.add(homePhone1);
    writer.println(toPhoneXML(mobilePhone1, homePhone1));
    writer.println("</cia>");
    writer.close();
    return phones;
  }

  private String toClientXML(ClientFromMigration client) {
    return "<client id=\"" + client.client_id + "\">"
      + "\n<surname value=\"" + client.surname + "\"/> "
      + "\n<name value=\"" + client.name + "\"/>"
      + "\n<gender value=\"" + client.gender + "\"/>"
      + "\n<birth value=\"" + client.birth + "\"/>"
      + "\n<charm value=\"" + client.charm + "\"/>"
      + "\n<patronymic value=\"" + client.patronymic + "\"/>"
      + "\n</client>";
  }

  private String toAddressXML(AddressFromMigration reg, AddressFromMigration fact) {
    return
      "<client id=\"" + reg.client_id + "\">"
        + "\n<address>"
        + "\n\t<fact street=\"" + fact.street + "\" house=\"" + fact.house + "\" flat=\"" + fact.flat + "\"/>"
        + "\n\t<register street=\"" + reg.street + "\" house=\"" + reg.house + "\" flat=\"" + reg.flat + "\"/>"
        + "\n</address>"
        + "\n</client>";
  }

  private String toPhoneXML(PhoneFromMigration mobile, PhoneFromMigration home) {
    return "<client id=\"" + mobile.client_id + "\">"
      + "\n<mobilePhone>" + mobile.number + "</mobilePhone>"
      + "\n<homePhone>" + home.number + "</homePhone>"
      + "\n</client>";
  }

  private void assertClients(List<ClientFromMigration> clients1, List<ClientFromMigration> clients2) {
    for (int i = 0; i < clients1.size(); i++) {
      assertThat(clients1.get(i).client_id).isEqualTo(clients2.get(i).client_id);
      assertThat(clients1.get(i).name).isEqualTo(clients2.get(i).name);
      assertThat(clients1.get(i).surname).isEqualTo(clients2.get(i).surname);
      assertThat(clients1.get(i).patronymic).isEqualTo(clients2.get(i).patronymic);
    }
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

}
