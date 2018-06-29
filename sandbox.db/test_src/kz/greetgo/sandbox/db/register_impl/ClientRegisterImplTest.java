package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @BeforeMethod
  public void clearDB() {
    clientTestDao.get().clearAllTables();
  }

  @Test
  public void getDetail() throws Exception {
    Integer clientId = RND.plusInt(100);
    ClientDetails details = generateRandomClientDetails(clientId);

    {
      insertClient(details);
    }

    //
    //
    //
    ClientDetails resultDetail = clientRegister.get().detail(clientId);
    //
    //
    //


    assertThat(resultDetail).isNotNull();
    assertThat(resultDetail.surname).isEqualTo(details.surname);
    assertThat(resultDetail.name).isEqualTo(details.name);
    assertThat(resultDetail.patronymic).isEqualTo(details.patronymic);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    assertThat(format.format(resultDetail.birthDate)).isEqualTo(format.format(details.birthDate));
    assertThat(resultDetail.charmId).isEqualTo(details.charmId);

    assertThat(resultDetail.addressFact).isNotNull();
    assertThat(resultDetail.addressFact.client).isEqualTo(details.addressFact.client);
    assertThat(resultDetail.addressFact.type).isEqualTo(details.addressFact.type);
    assertThat(resultDetail.addressFact.street).isEqualTo(details.addressFact.street);
    assertThat(resultDetail.addressFact.house).isEqualTo(details.addressFact.house);
    assertThat(resultDetail.addressFact.flat).isEqualTo(details.addressFact.flat);

    assertThat(resultDetail.addressReg).isNotNull();
    assertThat(resultDetail.addressReg.client).isEqualTo(details.addressReg.client);
    assertThat(resultDetail.addressReg.type).isEqualTo(details.addressReg.type);
    assertThat(resultDetail.addressReg.street).isEqualTo(details.addressReg.street);
    assertThat(resultDetail.addressReg.house).isEqualTo(details.addressReg.house);
    assertThat(resultDetail.addressReg.flat).isEqualTo(details.addressReg.flat);

    assertThat(resultDetail.homePhone).isNotNull();
    assertThat(resultDetail.homePhone.client).isEqualTo(details.homePhone.client);
    assertThat(resultDetail.homePhone.type).isEqualTo(details.homePhone.type);
    assertThat(resultDetail.homePhone.number).isEqualTo(details.homePhone.number);

    assertThat(resultDetail.workPhone).isNotNull();
    assertThat(resultDetail.workPhone.client).isEqualTo(details.workPhone.client);
    assertThat(resultDetail.workPhone.type).isEqualTo(details.workPhone.type);
    assertThat(resultDetail.workPhone.number).isEqualTo(details.workPhone.number);

    assertThat(resultDetail.mobilePhone).isNotNull();
    assertThat(resultDetail.mobilePhone.client).isEqualTo(details.mobilePhone.client);
    assertThat(resultDetail.mobilePhone.type).isEqualTo(details.mobilePhone.type);
    assertThat(resultDetail.mobilePhone.number).isEqualTo(details.mobilePhone.number);
  }

  @Test
  public void insertNewClient() throws Exception {
    ClientToSave clientToSave = generateRandomClientToSave(null);

    //
    //
    //
    clientRegister.get().save(clientToSave);
    //
    //
    //

    List<ClientDetails> detailsList;

    {
      detailsList = getClientDetailsList();
      assertThat(detailsList).isNotNull();
      assertThat(detailsList).hasSize(1);
    }

    ClientDetails details = detailsList.get(0);
    isEqual(details, clientToSave);
  }

  @Test
  public void editClient() throws Exception {
    Integer clientId = RND.plusInt(100);
    ClientToSave clientToSave = generateRandomClientToSave(clientId);

    {
      ClientDetails leftClient = generateRandomClientDetails(clientId);
      insertClient(leftClient);
    }


    //
    //
    //
    clientRegister.get().save(clientToSave);
    //
    //
    //

    List<ClientDetails> detailsList;

    {
      detailsList = getClientDetailsList();
      assertThat(detailsList).isNotNull();
      assertThat(detailsList).hasSize(1);
    }

    ClientDetails details = detailsList.get(0);
    isEqual(details, clientToSave);
  }

  private void isEqual(ClientDetails details, ClientToSave clientToSave) {
    assertThat(details.surname).isEqualTo(clientToSave.surname);
    assertThat(details.name).isEqualTo(clientToSave.name);
    assertThat(details.patronymic).isEqualTo(clientToSave.patronymic);

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    assertThat(details.birthDate).isNotNull();
    assertThat(format.format(details.birthDate)).isEqualTo(format.format(clientToSave.birthDate));

    assertThat(details.charmId).isEqualTo(clientToSave.charmId);
    assertThat(details.gender).isEqualTo(clientToSave.gender);

    assertThat(details.addressFact).isNotNull();
    assertThat(details.addressFact.client).isEqualTo(clientToSave.addressFact.client);
    assertThat(details.addressFact.type).isEqualTo(clientToSave.addressFact.type);
    assertThat(details.addressFact.street).isEqualTo(clientToSave.addressFact.street);
    assertThat(details.addressFact.house).isEqualTo(clientToSave.addressFact.house);
    assertThat(details.addressFact.flat).isEqualTo(clientToSave.addressFact.flat);

    assertThat(details.addressReg).isNotNull();
    assertThat(details.addressReg.client).isEqualTo(clientToSave.addressReg.client);
    assertThat(details.addressReg.type).isEqualTo(clientToSave.addressReg.type);
    assertThat(details.addressReg.street).isEqualTo(clientToSave.addressReg.street);
    assertThat(details.addressReg.house).isEqualTo(clientToSave.addressReg.house);
    assertThat(details.addressReg.flat).isEqualTo(clientToSave.addressReg.flat);

    assertThat(details.homePhone).isNotNull();
    assertThat(details.homePhone.client).isEqualTo(clientToSave.homePhone.client);
    assertThat(details.homePhone.type).isEqualTo(clientToSave.homePhone.type);
    assertThat(details.homePhone.number).isEqualTo(clientToSave.homePhone.number);

    assertThat(details.workPhone).isNotNull();
    assertThat(details.workPhone.client).isEqualTo(clientToSave.workPhone.client);
    assertThat(details.workPhone.type).isEqualTo(clientToSave.workPhone.type);
    assertThat(details.workPhone.number).isEqualTo(clientToSave.workPhone.number);

    assertThat(details.mobilePhone).isNotNull();
    assertThat(details.mobilePhone.client).isEqualTo(clientToSave.mobilePhone.client);
    assertThat(details.mobilePhone.type).isEqualTo(clientToSave.mobilePhone.type);
    assertThat(details.mobilePhone.number).isEqualTo(clientToSave.mobilePhone.number);
  }

  @Test
  public void deleteClient() throws Exception {

    Integer rClientId = RND.plusInt(100);

    {
      ClientDot leftDot = generateRandomClientDot();
      leftDot.id = rClientId;
      insertClient(leftDot);
    }

    //
    //
    //
    clientRegister.get().delete(rClientId);
    //
    //
    //

    {
      Integer actual = clientTestDao.get().getActual(rClientId);
      assertThat(actual).isNotNull();
      assertThat(actual).isEqualTo(0);
    }
  }


  @Test
  public void getRecordsWithEmptyFilter() throws Exception {

    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = 0;
    emptyFilter.limit = 10;

    {
      for (int i = 0; i < 40; i++) {
        Integer clientId = RND.plusInt(10000);
        ClientDetails details = generateRandomClientDetails(clientId);
        insertClient(details);
      }
    }

    //
    //
    //
    List<ClientRecord> clientRecordList = clientRegister.get().getRecords(emptyFilter);
    //
    //
    //

    assertThat(clientRecordList.size()).isEqualTo(emptyFilter.limit);
  }

  enum FioEnum {
    SURNAME, NAME, PATRONYMIC
  }

  @DataProvider
  public Object[][] filter_DP() {
    return new Object[][]{
      new Object[]{FioEnum.SURNAME},
      new Object[]{FioEnum.NAME},
      new Object[]{FioEnum.PATRONYMIC}
    };
  }

  @Test(dataProvider = "filter_DP")
  public void getRecordsWithFilter(FioEnum fioEnum) throws Exception {

    String rText = RND.str(5);

    ClientFilter filter = new ClientFilter();
    filter.fio = rText;

    {
      for (int i = 0; i < 20; i++) {
        Integer clientId = RND.plusInt(10000);
        ClientDetails leftDetails = generateRandomClientDetails(clientId);
        insertClient(leftDetails);
      }
    }

    ClientDetails details;

    {
      Integer clientId = RND.plusInt(10000);
      details = generateRandomClientDetails(clientId);

      switch (fioEnum) {
        case SURNAME:
          details.surname = rText;
          break;
        case NAME:
          details.name = rText;
          break;
        case PATRONYMIC:
          details.patronymic = rText;
      }

      insertClient(details);
    }
    //
    //
    //
    List<ClientRecord> clientRecordList = clientRegister.get().getRecords(filter);
    //
    //
    //

    assertThat(clientRecordList).isNotNull();
    assertThat(clientRecordList.size()).isEqualTo(1);

    assertThat(clientRecordList.get(0).id).isEqualTo(details.id);
    assertThat(clientRecordList.get(0).surname).isEqualTo(details.surname);
    assertThat(clientRecordList.get(0).name).isEqualTo(details.name);
    assertThat(clientRecordList.get(0).patronymic).isEqualTo(details.patronymic);
  }

  @DataProvider
  public Object[][] getRecords_WithSort_DP() {
    return new Object[][]{
      new Object[]{SortByEnum.FULL_NAME, SortDirection.ASCENDING},
      new Object[]{SortByEnum.AGE, SortDirection.ASCENDING},
      new Object[]{SortByEnum.MIDDLE_BALANCE, SortDirection.ASCENDING},
      new Object[]{SortByEnum.MAX_BALANCE, SortDirection.ASCENDING},
      new Object[]{SortByEnum.MIN_BALANCE, SortDirection.ASCENDING},
      new Object[]{SortByEnum.FULL_NAME, SortDirection.DESCENDING},
      new Object[]{SortByEnum.AGE, SortDirection.DESCENDING},
      new Object[]{SortByEnum.MIDDLE_BALANCE, SortDirection.DESCENDING},
      new Object[]{SortByEnum.MAX_BALANCE, SortDirection.DESCENDING},
      new Object[]{SortByEnum.MIN_BALANCE, SortDirection.DESCENDING}
    };
  }

  @Test(dataProvider = "getRecords_WithSort_DP")
  public void getRecords_WithSort(SortByEnum sortByEnum, SortDirection sortDirection) throws Exception {
    ClientFilter filter = new ClientFilter();
    filter.sortByEnum = sortByEnum;
    filter.sortDirection = sortDirection;

    List<ClientRecord> clientRecords = Lists.newArrayList();

    {
      for (int i = 0; i < 20; i++) {

        Integer clientId = RND.plusInt(Integer.MAX_VALUE);

        ClientDetails details = generateRandomClientDetails(clientId);
        insertClient(details);

        generateRandomAccountsFor(details.id, RND.plusInt(50));

        clientRecords.add(fromDetails(details));
      }
    }

    sortList(clientRecords, sortByEnum, sortDirection);


    //
    //
    //
    List<ClientRecord> result = clientRegister.get().getRecords(filter);
    //
    //
    //

    assertThat(result).isNotNull();
    assertThat(result).hasSize(clientRecords.size());

    for (int i = 0; i < result.size(); i++) {
      assertThat(result.get(i).id).isEqualTo(clientRecords.get(i).id);
    }
  }


  @Test
  public void getRecordsCountWithEmptyFilter() throws Exception {

    int randomCount = RND.plusInt(40);

    ClientFilter filter = new ClientFilter();

    {
      for (int i = 0; i < randomCount; i++) {
        Integer clientId = RND.plusInt(10000);
        ClientDetails details = generateRandomClientDetails(clientId);
        insertClient(details);
      }
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(filter);
    //
    //
    //

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(randomCount);
  }

  @Test(dataProvider = "filter_DP")
  public void getRecordsCountWithFilter(FioEnum fioEnum) throws Exception {

    int randomCount = RND.plusInt(40);
    String rFio = RND.str(10);

    ClientFilter filter = new ClientFilter();
    filter.fio = rFio;

    {
      for (int i = 0; i < randomCount; i++) {
        Integer clientId = RND.plusInt(Integer.MAX_VALUE);
        ClientDetails details = generateRandomClientDetails(clientId);
        insertClient(details);
      }
    }

    {
      Integer clientId = RND.plusInt(Integer.MAX_VALUE);
      ClientDetails details = generateRandomClientDetails(clientId);
      switch (fioEnum) {
        case SURNAME:
          details.surname = rFio;
          break;
        case NAME:
          details.name = rFio;
          break;
        case PATRONYMIC:
          details.patronymic = rFio;
      }
      insertClient(details);
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(filter);
    //
    //
    //

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(1);
  }

  @Test
  public void getCharms() throws Exception {
    int randomSize = RND.plusInt(20);
    List<CharmRecord> charmRecords = new ArrayList<>();

    {
      for (int i = 0; i < randomSize; i++) {
        CharmRecord charmRecord = new CharmRecord(RND.plusInt(Integer.MAX_VALUE), RND.str(10), RND.str(10), RND.rnd.nextFloat());
        insertCharm(charmRecord);
        charmRecords.add(charmRecord);
      }
    }

    //
    //
    //
    List<CharmRecord> resultCharmRecords = clientRegister.get().getCharms();
    //
    //
    //

    assertThat(resultCharmRecords).isNotNull();
    assertThat(resultCharmRecords).hasSize(randomSize);

    for (int i = 0; i < randomSize; i++)
      assertThat(resultCharmRecords.get(i).id).isEqualTo(charmRecords.get(i).id);

  }

  class TestRender implements ClientRender {

    private String name;
    private List<ClientRow> clientRows;

    public TestRender() {
      clientRows = Lists.newArrayList();
    }

    @Override
    public void start(String name, Date contractDate) {
      this.name = name;
    }

    @Override
    public void append(ClientRow asdRow) {
      this.clientRows.add(asdRow);
    }

    @Override
    public void finish() {
    }
  }

  @Test
  public void renderClientList() {
    TestRender render = new TestRender();

    String name = RND.str(10);
    ClientDetails leftDetails;

    ClientFilter filter = new ClientFilter();

    {
      leftDetails = generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE));
      insertClient(leftDetails);
    }

    //
    //
    //
    clientRegister.get().renderClientList(name, filter, render);
    //
    //
    //

    assertThat(render.name).isEqualTo(name);
    assertThat(render.clientRows).hasSize(1);
    assertThat(render.clientRows.get(0).id).isEqualTo(leftDetails.id);
  }

  @Test(dataProvider = "filter_DP")
  public void renderClientList_withFilter(FioEnum fioEnum) {
    TestRender render = new TestRender();

    String name = RND.str(10);

    String rFio = RND.str(10);

    ClientFilter filter = new ClientFilter();
    filter.fio = rFio;

    ClientDetails leftDetails;

    {
      leftDetails = generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE));
      switch (fioEnum) {
        case SURNAME:
          leftDetails.surname = RND.str(10) + rFio + RND.str(10);
          break;
        case NAME:
          leftDetails.name = RND.str(10) + rFio + RND.str(10);
          break;
        case PATRONYMIC:
          leftDetails.patronymic = RND.str(10) + rFio + RND.str(10);
      }
      insertClient(leftDetails);
    }

    {
      for (int i = 0; i < 10; i++)
        insertClient(generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE)));
    }

    //
    //
    //
    clientRegister.get().renderClientList(name, filter, render);
    //
    //
    //
    assertThat(render.name).isEqualTo(name);
    assertThat(render.clientRows).hasSize(1);
    assertThat(render.clientRows.get(0).id).isEqualTo(leftDetails.id);
  }

  public List<ClientDetails> getClientDetailsList() {
    List<ClientDetails> clientDetails = Lists.newArrayList();
    for (Client client : clientTestDao.get().getClients())
      clientDetails.add(fromClient(client));
    return clientDetails;
  }

  public ClientDetails fromClient(Client client) {
    ClientDetails details = new ClientDetails();
    details.id = client.id;
    details.surname = client.surname;
    details.name = client.name;
    details.patronymic = client.patronymic;
    details.gender = client.gender;
    details.birthDate = client.birthDate;
    details.charmId = client.charmId;
    details.addressFact = clientTestDao.get().getClientAddress(client.id, AddressTypeEnum.FACT);
    details.addressReg = clientTestDao.get().getClientAddress(client.id, AddressTypeEnum.REG);
    details.homePhone = clientTestDao.get().getClientPhone(client.id, PhoneType.HOME);
    details.workPhone = clientTestDao.get().getClientPhone(client.id, PhoneType.WORK);
    details.mobilePhone = clientTestDao.get().getClientPhone(client.id, PhoneType.MOBILE);
    return details;
  }

  public ClientRecord fromDetails(ClientDetails details) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = details.id;
    clientRecord.surname = details.surname;
    clientRecord.name = details.name;
    clientRecord.patronymic = details.patronymic;
    clientRecord.age = getAge(details.birthDate);
    List<ClientAccount> clientAccounts = clientTestDao.get().getClientAccounts(details.id);
    clientRecord.middle_balance = getMiddleBalance(clientAccounts);
    clientRecord.max_balance = getMaxBalance(clientAccounts);
    clientRecord.min_balance = getMinBalance(clientAccounts);
    return clientRecord;
  }

  public static int getAge(Date dateOfBirth) {
    Calendar today = Calendar.getInstance();
    Calendar birthDate = Calendar.getInstance();
    int age;
    birthDate.setTime(dateOfBirth);
    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
    if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
      (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
      age--;
    } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
      (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
      age--;
    }
    return ++age;
  }

  private float getMinBalance(List<ClientAccount> clientAccounts) {
    float min_balance = Integer.MAX_VALUE;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money < min_balance) min_balance = clientAccount.money;
    return min_balance;
  }

  private float getMaxBalance(List<ClientAccount> clientAccounts) {
    float max_balance = -1;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money > max_balance) max_balance = clientAccount.money;
    return max_balance;
  }

  private float getMiddleBalance(List<ClientAccount> clientAccounts) {
    float middle_balance = 0;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      middle_balance += clientAccount.money;
    return middle_balance / clientAccounts.size();
  }

  public void insertClient(ClientDetails details) {
    CharmRecord charmRecord = new CharmRecord(details.charmId, RND.str(10), RND.str(10), (float) RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charmId);
    insertClientAddress(details.addressFact);
    insertClientAddress(details.addressReg);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);
  }

  public void insertClient(ClientDot clientDot) {
    CharmRecord charmRecord = new CharmRecord(clientDot.charmId, RND.str(10), RND.str(10), (float) RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    clientTestDao.get().insertClient(clientDot.id, clientDot.surname, clientDot.name, clientDot.patronymic, clientDot.gender, clientDot.birthDate, clientDot.charmId);
  }

  public ClientToSave generateRandomClientToSave(Integer id) {
    ClientDetails details = generateRandomClientDetails(id);
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.id = details.id;
    clientToSave.surname = details.surname;
    clientToSave.name = details.name;
    clientToSave.patronymic = details.patronymic;
    clientToSave.birthDate = details.birthDate;
    clientToSave.gender = details.gender;
    clientToSave.charmId = details.charmId;
    clientToSave.addressFact = details.addressFact;
    clientToSave.addressReg = details.addressReg;
    clientToSave.homePhone = details.homePhone;
    clientToSave.mobilePhone = details.mobilePhone;
    clientToSave.workPhone = details.workPhone;
    CharmRecord charmRecord = new CharmRecord(details.charmId, RND.str(10), RND.str(10), (float) RND.plusDouble(Double.MAX_VALUE, 10));
    insertCharm(charmRecord);
    return clientToSave;
  }

  public ClientDot generateRandomClientDot() {
    ClientDot clientDot = new ClientDot();
    clientDot.id = RND.plusInt(Integer.MAX_VALUE);
    clientDot.surname = RND.str(10);
    clientDot.name = RND.str(10);
    clientDot.patronymic = RND.str(10);
    clientDot.birthDate = RND.dateYears(10, 20);
    clientDot.charmId = RND.plusInt(Integer.MAX_VALUE);
    clientDot.gender = GenderEnum.MALE;
    return clientDot;
  }

  public ClientDetails generateRandomClientDetails(Integer id) {
    ClientDetails details = new ClientDetails();
    details.id = id;
    details.surname = RND.intStr(10);
    details.name = RND.intStr(10);
    details.patronymic = RND.intStr(10);
    details.birthDate = RND.dateYears(0, 1000);
    details.gender = GenderEnum.MALE;
    details.charmId = RND.plusInt(Integer.MAX_VALUE);
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    return details;
  }

  public void generateRandomAccountsFor(Integer id, int i) {
    for (int c = 0; c < i; c++) {
      ClientAccount clientAccount = new ClientAccount();
      clientAccount.client = id;
      clientAccount.number = RND.intStr(11);
      clientAccount.money = (float) RND.plusDouble(5000, 0);
      clientAccount.registeredAt = new Date();
      insertClientAccount(clientAccount);
    }
  }

  private void insertClientAddress(ClientAddress clientAddress) {
    clientTestDao.get().insertClientAddress(clientAddress.client, clientAddress.type, clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private void insertClientPhone(ClientPhone clientPhone) {
    clientTestDao.get().insertClientPhone(clientPhone.client, clientPhone.number, clientPhone.type);
  }

  private void insertClientAccount(ClientAccount account) {
    clientTestDao.get().insertClientAccount(account.client, account.number, account.money, account.registeredAt);
  }

  public void insertCharm(CharmRecord charmRecord) {
    clientTestDao.get().insertCharm(charmRecord.id, charmRecord.name, charmRecord.description, charmRecord.energy);
  }

  public static void sortList(List<ClientRecord> clientRecords, SortByEnum sortBy, SortDirection sortDirection) {
    Comparator<ClientRecord> comparator = null;
    switch (sortBy) {
      case NONE:
        comparator = null;
        break;
      case FULL_NAME:
        comparator = Comparator.comparing(o -> o.surname);
        comparator.thenComparing(o -> o.name);
        comparator.thenComparing(o -> o.patronymic);
        break;
      case AGE:
        comparator = Comparator.comparing(o -> o.age);
        break;
      case MIDDLE_BALANCE:
        comparator = Comparator.comparing(o -> o.middle_balance);
        break;
      case MIN_BALANCE:
        comparator = Comparator.comparing(o -> o.min_balance);
        break;
      case MAX_BALANCE:
        comparator = Comparator.comparing(o -> o.max_balance);
        break;
    }
    if (comparator != null) clientRecords.sort(comparator);

    if (sortDirection != null)
      switch (sortDirection) {
        case DESCENDING:
          Collections.reverse(clientRecords);
          break;
      }
  }

}