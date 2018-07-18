package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.worker.impl.CIAWorker;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

public class CIAWorkerTest extends WorkerTest {

  private final String clientTmpTableName = "cia_migration_client";
  private final String clientAddressTmpTableName = "cia_migration_client_address";
  private final String clientPhoneTmpTableName = "cia_migration_client_phone";

  @BeforeMethod
  public void beforeMethod() {
    migrationDao.get().clearClientTable();
  }

  @AfterMethod
  public void afterMethod() {
    removeTmpTables(getCiaTmpTableNames());
  }

  @Test
  public void fillCiaTmpTables() throws Exception {
    Integer randomSize = RND.plusInt(100);
    List<TestClient> leftTestClients = getRandomTestClientList(randomSize);

    Connection connection = getConnection();
    InputStream tmpCsvFileInputStream = getInputStream(getCiaTestFileName(), getCiaString(leftTestClients));

    //
    //
    //
    CIAWorker worker = getCiaWorker(connection, tmpCsvFileInputStream);
    worker.fillTmpTables();
    //
    //
    //

    checkInTmpTables(leftTestClients);

    connection.close();
  }

  @Test
  public void validTmpTables() throws Exception {
    String clientTmpTableName = getNameWithDate(this.clientTmpTableName);

    Integer randomSize = RND.plusInt(100);
    List<TestTmpClient> leftTestClients = getRandomTestTmpClientList(randomSize);

    {
      toErrorList(leftTestClients);
      migrationDao.get().createClientTmpTable(clientTmpTableName);
      leftTestClients.forEach(testClient -> insertTestTmpClient(testClient, clientTmpTableName));
    }

    Connection connection = getConnection();

    //
    //
    //
    CIAWorker ciaWorker = getCiaWorker(connection, null);
    ciaWorker.setTmpTableNames(clientTmpTableName, null, null);
    ciaWorker.validTmpTables();
    //
    //
    //

    List<String> ciaTmpTablesName = getCiaTmpTableNames();

    List<TestTmpClient> tmpClients = getTmpClientList(getClientTmpTableName(ciaTmpTablesName));

    assertThat(tmpClients).hasSize(randomSize);
    assertThat(tmpClients).containsAll(leftTestClients);

    connection.close();
  }

  @Test
  public void margeTmpTables() throws Exception {
    String clientTmpTableName = getNameWithDate(this.clientTmpTableName);
    String clientAddressTmpTableName = getNameWithDate(this.clientAddressTmpTableName);
    String clientPhoneTmpTableName = getNameWithDate(this.clientPhoneTmpTableName);

    Integer randomSize = RND.plusInt(100);
    List<TestClient> leftTestClients =  getRandomTestClientList(randomSize);

    {
      toNotMargeList(leftTestClients);
      insertToTmpTables(leftTestClients, clientTmpTableName, clientAddressTmpTableName, clientPhoneTmpTableName);
    }

    Connection connection = getConnection();

    //
    //
    //
    CIAWorker ciaWorker = getCiaWorker(connection, null);
    ciaWorker.setTmpTableNames(clientTmpTableName, clientAddressTmpTableName, clientPhoneTmpTableName);
    ciaWorker.margeTmpTables();
    //
    //
    //

    checkInTmpTables(margeList(leftTestClients));

    connection.close();
  }

  @Test
  public void migrateToTables() throws Exception {
    Integer randomSize = RND.plusInt(10);
    List<TestClient> leftTestClients = getRandomTestClientList(randomSize);

    {
      toNotMargeList(leftTestClients);
      toErrorList(leftTestClients.stream().map(testClient -> testClient.tmpTestClient).collect(Collectors.toList()));
    }

    Connection connection = getConnection();
    File tmpFile = createTmpFile(getCiaTestFileName(), getCiaString(leftTestClients));

    //
    //
    //
    migration.get().migrate(connection, tmpFile);
    //
    //
    //

    leftTestClients = margeList(leftTestClients);
    leftTestClients = removeInvalidClients(leftTestClients);

    checkInTables(leftTestClients);

    tmpFile.delete();
    connection.close();
  }

  private List<TestClient> removeInvalidClients(List<TestClient> leftTestClients) {
    List<TestClient> testClients = Lists.newArrayList();
    for (TestClient testClient : leftTestClients) {
      if (testClient.tmpTestClient.name != null && testClient.tmpTestClient.surname != null &&
        testClient.tmpTestClient.birthDate != null) testClients.add(testClient);
    }
    return testClients;
  }

  private void checkInTables(List<TestClient> leftTestClients) {
    List<Client> clientList = migrationDao.get().getClients();
    List<ClientAddress> clientAddressList = migrationDao.get().getClientAddresses();
    List<ClientPhone> clientPhoneList = migrationDao.get().getClientPhones();

    assertThat(clientList).hasSize(leftTestClients.size());
    assertThat(clientAddressList).hasSize(getAddressesSize(leftTestClients));
    assertThat(clientPhoneList).hasSize(getPhonesSize(leftTestClients));

    leftTestClients.sort(Comparator.comparing(o -> o.tmpTestClient.id));

    for (int i = 0; i < leftTestClients.size(); i++) {
      isEqual(clientList.get(i), leftTestClients.get(i).tmpTestClient);
    }

  }

  private void isEqual(Client client, TestTmpClient tmpTestClient) {
    assertThat(client.ciaId).isEqualTo(tmpTestClient.id);
    assertThat(client.surname).isEqualTo(tmpTestClient.surname);
    assertThat(client.name).isEqualTo(tmpTestClient.name);
    assertThat(client.patronymic).isEqualTo(tmpTestClient.patronymic);
    assertThat(client.gender.name()).isEqualTo(tmpTestClient.gender);
    isEqualDate(client.birthDate, tmpTestClient.birthDate);
  }

  private void isEqualDate(Date date1, String date2) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(format.format(date1)).isEqualTo(date2);
  }

  private List<TestClient> margeList(List<TestClient> leftTestClients) {
    List<TestClient> margeTestClientList = Lists.newArrayList();
    for (TestClient testClient : leftTestClients) {
      TestClient include = include(margeTestClientList, testClient.tmpTestClient);
      if (include == null) {
        margeTestClientList.add(testClient);
        continue;
      }
      if (testClient.tmpTestClient.patronymic != null) include.tmpTestClient.patronymic = testClient.tmpTestClient.patronymic;
      if (testClient.tmpTestClient.surname != null) include.tmpTestClient.surname = testClient.tmpTestClient.surname;
      if (testClient.tmpTestClient.birthDate != null) include.tmpTestClient.birthDate = testClient.tmpTestClient.birthDate;
      if (testClient.tmpTestClient.name != null) include.tmpTestClient.name = testClient.tmpTestClient.name;
      if (testClient.tmpTestClient.gender != null) include.tmpTestClient.gender = testClient.tmpTestClient.gender;
      if (testClient.tmpTestClient.charm != null) include.tmpTestClient.charm = testClient.tmpTestClient.charm;
      if (testClient.homePhone.number != null) include.homePhone.number = testClient.homePhone.number;
      if (testClient.workPhone.number != null) include.workPhone.number = testClient.workPhone.number;
      if (testClient.mobilePhone.number != null) include.mobilePhone.number = testClient.mobilePhone.number;
    }
    return margeTestClientList;
  }

  private TestClient include(List<TestClient> margeTestClientList, TestTmpClient tmpTestClient) {
    for (TestClient testClient : margeTestClientList)
      if (testClient.tmpTestClient.id.equals(tmpTestClient.id)) return testClient;
    return null;
  }


  private void toNotMargeList(List<TestClient> leftTestClients) {
    String id = null;
    for (TestClient testClient : leftTestClients) {
      if (id == null) {
        id = testClient.tmpTestClient.id;
      } else if (RND.bool()) {
        testClient.tmpTestClient.id = id;
        testClient.mobilePhone.client = id;
        testClient.workPhone.client = id;
        testClient.homePhone.client = id;
        testClient.addressReg.client = id;
        testClient.addressFact.client = id;
        id = null;
        if (RND.bool()) {
          if (RND.bool()) testClient.tmpTestClient.patronymic = null;
          if (RND.bool()) testClient.tmpTestClient.gender = null;
          if (RND.bool()) testClient.tmpTestClient.charm = null;
          if (RND.bool()) testClient.homePhone.number = null;
          if (RND.bool()) testClient.workPhone.number = null;
          if (RND.bool()) testClient.mobilePhone.number = null;
        }
      }
    }
  }

  private void checkInTmpTables(List<TestClient> leftTestClients) {
    List<String> ciaTmpTablesName = getCiaTmpTableNames();

    List<TestTmpClient> tmpClients = getTmpClientList(getClientTmpTableName(ciaTmpTablesName));
    List<TestTmpClientPhone> tmpClientPhones = getTmpClientPhoneList(getClientPhoneTmpTableName(ciaTmpTablesName));
    List<TestTmpClientAddress> tmpClientAddresses = getTmpClientAddress(getClientAddressTmpTableName(ciaTmpTablesName));

    assertThat(tmpClients).hasSize(leftTestClients.size());
    assertThat(tmpClientPhones).hasSize(getPhonesSize(leftTestClients));
    assertThat(tmpClientAddresses).hasSize(getAddressesSize(leftTestClients));

    for (int i = 0; i < leftTestClients.size(); i++) {
      assertThat(tmpClients.get(i)).isEqualsToByComparingFields(leftTestClients.get(i).tmpTestClient);
      isContainPhones(leftTestClients.get(i), getTmpClientPhoneList(leftTestClients.get(i).tmpTestClient.id, tmpClientPhones));
      isContainAddresses(leftTestClients.get(i), getTmpClientAddressList(leftTestClients.get(i).tmpTestClient.id, tmpClientAddresses));
    }
  }

  private int getAddressesSize(List<TestClient> leftTestClients) {
    int count = 0;
    for (TestClient testClient : leftTestClients) {
      if (testClient.addressFact != null) count++;
      if (testClient.addressReg != null) count++;
    }
    return count;
  }

  private int getPhonesSize(List<TestClient> leftTestClients) {
    int count = 0;
    for (TestClient testClient : leftTestClients) {
      if (testClient.homePhone != null) count++;
      if (testClient.mobilePhone != null) count++;
      if (testClient.workPhone != null) count++;
    }
    return count;
  }

  private void insertToTmpTables(List<TestClient> testClients, String clientTmpTableName, String clientAddressTmpTableName, String clientPhoneTmpTableName) {
    migrationDao.get().createClientTmpTable(clientTmpTableName);
    migrationDao.get().createClientAddressTmpTable(clientAddressTmpTableName);
    migrationDao.get().createClientPhoneTmpTable(clientPhoneTmpTableName);

    testClients.forEach(testClient -> {
      insertTestTmpClient(testClient.tmpTestClient, clientTmpTableName);
      insertTestTmpClientAddress(testClient.addressFact, clientAddressTmpTableName);
      insertTestTmpClientAddress(testClient.addressReg,clientAddressTmpTableName);
      insertTestTmpClientPhone(testClient.homePhone,clientPhoneTmpTableName);
      insertTestTmpClientPhone(testClient.workPhone,clientPhoneTmpTableName);
      insertTestTmpClientPhone(testClient.mobilePhone,clientPhoneTmpTableName);
    });
  }

  private void insertTestTmpClientPhone(TestTmpClientPhone mobilePhone, String clientPhoneTmpTableName) {
    migrationDao.get().insertClientPhone(clientPhoneTmpTableName, mobilePhone.client, mobilePhone.number, mobilePhone.type);
  }

  private void insertTestTmpClientAddress(TestTmpClientAddress addressFact, String clientAddressTmpTableName) {
    migrationDao.get().insertClientAddress(clientAddressTmpTableName, addressFact.client, addressFact.type, addressFact.street, addressFact.house, addressFact.flat);
  }

  private void insertTestTmpClient(TestTmpClient testClient, String tmpTable) {
    migrationDao.get().insertClient(tmpTable, testClient.id, testClient.surname, testClient.name,
      testClient.patronymic, testClient.birthDate, testClient.gender, testClient.charm);
  }

  private void toErrorList(List<TestTmpClient> testClients) {
    testClients.forEach(testClient -> {
      if (RND.bool()) {
        if (RND.bool()) {
          testClient.name = null;
          testClient.error = MigrationError.CIA.NAME_NOT_FOUND;
        }
        else if (RND.bool()) {
          testClient.surname = null;
          testClient.error = MigrationError.CIA.SURNAME_NOT_FOUND;
        }
        else if (RND.bool()) {
          testClient.birthDate = null;
          testClient.error = MigrationError.CIA.BIRTH_DATE_NOT_FOUND;
        } else {
          testClient.error = null;
        }
      }
    });
  }

  private String getCiaTestFileName() {
    return getNameWithDate("cia_test")+".xml";
  }

  private void isContainPhones(TestClient testClient, List<TestTmpClientPhone> tmpClientPhones) {
    isEqual(tmpClientPhones, testClient.homePhone);
    isEqual(tmpClientPhones, testClient.mobilePhone);
    isEqual(tmpClientPhones, testClient.workPhone);
  }

  private void isEqual(List<TestTmpClientPhone> tmpClientPhones, TestTmpClientPhone phone) {
    assertThat(tmpClientPhones.stream().filter(tmpPhone -> tmpPhone.type.equals(phone.type)).findFirst().get()).isEqualsToByComparingFields(phone);
  }

  private void isContainAddresses(TestClient testClient, List<TestTmpClientAddress> tmpClientAddresses) {
    isEqual(tmpClientAddresses, testClient.addressFact);
    isEqual(tmpClientAddresses, testClient.addressReg);
  }

  private void isEqual(List<TestTmpClientAddress> tmpClientAddresses, TestTmpClientAddress address) {
    assertThat(tmpClientAddresses.stream().filter(tmpClientAddress -> tmpClientAddress.type == address.type)
      .findFirst().get()).isEqualsToByComparingFields(address);
  }

  private List<TestTmpClientPhone> getTmpClientPhoneList(String cia_id , List<TestTmpClientPhone> tmpClientPhones) {
    return tmpClientPhones.stream().filter(tmpClientPhone -> tmpClientPhone.client.equals(cia_id)).collect(Collectors.toList());
  }

  private List<TestTmpClientAddress> getTmpClientAddressList(String cia_id, List<TestTmpClientAddress> tmpClientAddresses) {
    return tmpClientAddresses.stream().filter(tmpClientAddress -> tmpClientAddress.client.equals(cia_id)).collect(Collectors.toList());
  }

  private String getCiaString(List<TestClient> testClients) {
    StringBuilder cia = new StringBuilder();
    cia.append("<cia>\n");
    for (TestClient client : testClients) cia.append(client.toXml());
    cia.append("</cia>\n");
    return cia.toString();
  }

  private String getClientTmpTableName(List<String> ciaTmpTablesName) {
    return ciaTmpTablesName.stream().filter(name -> name.startsWith(clientTmpTableName)
      && !name.startsWith(clientAddressTmpTableName) && !name.startsWith(clientPhoneTmpTableName)).findFirst().get();
  }

  private String getClientPhoneTmpTableName(List<String> ciaTmpTablesName) {
    return ciaTmpTablesName.stream().filter(name -> name.startsWith(clientPhoneTmpTableName)).findFirst().get();
  }

  private String getClientAddressTmpTableName(List<String> ciaTmpTablesName) {
    return ciaTmpTablesName.stream().filter(name -> name.startsWith(clientAddressTmpTableName)).findFirst().get();
  }

  private List<TestTmpClient> getTmpClientList(String ciaClientTmpTableName) {
    return migrationDao.get().getTmpClientFromTable(ciaClientTmpTableName);
  }

  private List<TestTmpClientPhone> getTmpClientPhoneList(String ciaClientPhoneTmpTableName) {
    return migrationDao.get().getTmpClientPhones(ciaClientPhoneTmpTableName);
  }

  private List<TestTmpClientAddress> getTmpClientAddress(String clientAddressTmpTableName) {
    return migrationDao.get().getTmpClientAddresses(clientAddressTmpTableName);
  }

  private List<TestTmpClient> getRandomTestTmpClientList(int count) {
    List<TestTmpClient> testTmpClients = Lists.newArrayList();
    for (int i = 0 ; i < count; i++) testTmpClients.add(getRandomTmpTestClient());
    return testTmpClients;
  }

  private List<TestClient> getRandomTestClientList(int count) {
    List<TestClient> testClients = Lists.newArrayList();
    for (int i = 0; i < count; i++)
      testClients.add(getRandomTestClient());
    return testClients;
  }

  private TestClient getRandomTestClient() {
    TestClient client = new TestClient();
    client.tmpTestClient = getRandomTmpTestClient();
    client.addressFact = getRandomTestTmpAddress(client.tmpTestClient.id, AddressTypeEnum.FACT);
    client.addressReg = getRandomTestTmpAddress(client.tmpTestClient.id, AddressTypeEnum.REG);
    client.homePhone = getRandomTmpPhone(client.tmpTestClient.id, PhoneType.HOME.name());
    client.mobilePhone = getRandomTmpPhone(client.tmpTestClient.id, PhoneType.MOBILE.name());
    client.workPhone = getRandomTmpPhone(client.tmpTestClient.id, PhoneType.WORK.name());
    return client;
  }

  private TestTmpClient getRandomTmpTestClient() {
    TestTmpClient testTmpClient = new TestTmpClient();
    testTmpClient.id = RND.str(10);
    testTmpClient.surname = RND.str(10);
    testTmpClient.name = RND.str(10);
    testTmpClient.birthDate = getRandomDate("yyyy-MM-dd");
    testTmpClient.patronymic = RND.str(10);
    testTmpClient.charm = RND.str(10);
    testTmpClient.gender = RND.bool()?GenderEnum.MALE.name():GenderEnum.FEMALE.name();
    return testTmpClient;
  }

  private TestTmpClientPhone getRandomTmpPhone(String client, String type) {
    TestTmpClientPhone tmpClientPhone = new TestTmpClientPhone();
    tmpClientPhone.client = client;
    tmpClientPhone.type = type;
    tmpClientPhone.number = getTmpPhone();
    return tmpClientPhone;
  }

  private String getTmpPhone() {
    return "+7-" + RND.intStr(3) + "-" + RND.intStr(3) + "-" + RND.intStr(2) + "-" + RND.intStr(2);
  }

  private TestTmpClientAddress getRandomTestTmpAddress(String client, AddressTypeEnum type) {
    TestTmpClientAddress tmp = new TestTmpClientAddress();
    tmp.client = client;
    tmp.type = type;
    tmp.street = RND.str(10);
    tmp.house = RND.str(10);
    tmp.flat = RND.str(10);
    return tmp;
  }

  public static class TestTmpClient extends TMPClient {
    String error;

    String toXml() {
      StringBuilder sb = new StringBuilder();
      if (name != null) sb.append(buildRow("name", "value", name));
      if (surname != null) sb.append(buildRow("surname", "value", surname));
      if (patronymic != null) sb.append(buildRow("patronymic", "value", patronymic));
      if (birthDate != null) sb.append(buildRow("birth", "value", birthDate));
      if (gender != null) sb.append(buildRow("gender", "value", gender));
      if (charm != null) sb.append(buildRow("charm", "value", charm));
      return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) return false;
      if (!(obj instanceof TestTmpClient)) return false;
      TestTmpClient o = (TestTmpClient) obj;
      return Objects.equals(o.id, id) && Objects.equals(o.surname, surname)
        && Objects.equals(o.error, error) && Objects.equals(o.name, name)
        && Objects.equals(o.patronymic, patronymic) && Objects.equals(o.birthDate, birthDate)
        && Objects.equals(o.charm, charm) && Objects.equals(o.gender, gender);
    }
  }

  public static class TestTmpClientAddress extends TMPClientAddress {
    String toXml(String type) {
      return buildRow(type, "street", street, "flat", flat, "house", house);
    }
  }

  public static class TestTmpClientPhone extends TMPClientPhone {
    String toXml(String type) {
      return buildPhoneRow(type, number);
    }
    private String buildPhoneRow(String name, String value) {
      return "<" + name + ">" + value + "</" + name + ">\n";
    }
  }

  class TestClient {
    TestTmpClient tmpTestClient;
    TestTmpClientAddress addressFact;
    TestTmpClientAddress addressReg;
    TestTmpClientPhone homePhone;
    TestTmpClientPhone workPhone;
    TestTmpClientPhone mobilePhone;

    String toXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<client id=\"").append(tmpTestClient.id).append("\">\n");
      sb.append(tmpTestClient.toXml());
      sb.append("<address>\n");
      if (addressFact != null) sb.append(addressFact.toXml("fact"));
      if (addressReg != null) sb.append(addressReg.toXml("register"));
      sb.append("</address>\n");
      if (homePhone != null) sb.append(homePhone.toXml("homePhone"));
      if (homePhone != null) sb.append(workPhone.toXml("workPhone"));
      if (homePhone != null) sb.append(mobilePhone.toXml("mobilePhone"));
      sb.append("</client>\n");
      return sb.toString();
    }

    @Override
    public String toString() {
      return toXml();
    }
  }

  private static String buildRow(String name, String... vls) {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(name).append(" ");
    for (int i = 0; i < vls.length; i+=2)
      sb.append(vls[i]).append("=\"").append(vls[i+1]).append("\" ");
    return sb.append("/>\n").toString();
  }
}