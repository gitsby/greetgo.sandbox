package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientAccount;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.model.ClientPhone;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.GenderEnum;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.controller.model.SortByEnum;
import kz.greetgo.sandbox.controller.model.SortDirection;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @BeforeMethod
  public void clearDB() {
    clientTestDao.get().clearAllTables();
  }

  @Test
  public void getDetail() {
    Integer clientId = RND.plusInt(Integer.MAX_VALUE);
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

    isEqual(resultDetail, details);
  }

  private void isEqual(ClientDetails d1, ClientDetails d2) {
    assertThat(d1).isNotNull();
    assertThat(d1).isEqualsToByComparingFields(d2);

    isEqual(d1.birthDate, d2.birthDate);

    isEqual(d1.addressReg, d2.addressReg);
    isEqual(d1.addressFact, d2.addressFact);

    isEqual(d1.homePhone, d2.homePhone);
    isEqual(d1.workPhone, d2.workPhone);
    isEqual(d1.mobilePhone, d2.mobilePhone);
  }

  private void isEqual(Date d1, Date d2) {
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    assertThat(format.format(d1)).isEqualTo(format.format(d2));
  }

  private void isEqual(ClientAddress a1, ClientAddress a2) {
    assertThat(a1).isNotNull();
    assertThat(a1).isEqualsToByComparingFields(a2);
  }

  private void isEqual(ClientPhone p1, ClientPhone p2) {
    assertThat(p1).isNotNull();
    assertThat(p1).isEqualsToByComparingFields(p2);
  }

  @Test
  public void insertNewClient() {
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

    isEqual(detailsList.get(0), clientToSave);
  }

  private void isEqual(ClientDetails details, ClientToSave clientToSave) {
    ClientDot clientDot = getClientDot(details.id);

    ClientAddressDot addressFactDot = getClientAddressDot(details.id, AddressTypeEnum.FACT);
    ClientAddressDot addressRegDot = getClientAddressDot(details.id, AddressTypeEnum.REG);
    ClientPhoneDot homePhoneDot = getClientPhoneDot(details.id, PhoneType.HOME);
    ClientPhoneDot workPhoneDot = getClientPhoneDot(details.id, PhoneType.WORK);
    ClientPhoneDot mobilePhoneDot = getClientPhoneDot(details.id, PhoneType.MOBILE);

    assertThat(clientDot.surname).isEqualTo(clientToSave.surname);
    assertThat(clientDot.name).isEqualTo(clientToSave.name);
    assertThat(clientDot.patronymic).isEqualTo(clientToSave.patronymic);
    assertThat(clientDot.gender).isEqualTo(clientToSave.gender);
    assertThat(clientDot.charmId).isEqualTo(clientToSave.charmId);

    isEqual(clientDot.birthDate, clientToSave.birthDate);

    isEqual(addressFactDot, clientToSave.addressFact);
    isEqual(addressRegDot, clientToSave.addressReg);

    isEqual(homePhoneDot, clientToSave.homePhone);
    isEqual(workPhoneDot, clientToSave.workPhone);
    isEqual(mobilePhoneDot, clientToSave.mobilePhone);
  }

  private void isEqual(ClientPhoneDot phoneDot, ClientPhone phone) {
    assertThat(phoneDot).isNotNull();
    assertThat(phoneDot.client).isEqualTo(phone.client);
    assertThat(phoneDot.type).isEqualTo(phone.type);
    assertThat(phoneDot.number).isEqualTo(phone.number);
  }

  private void isEqual(ClientAddressDot addressDot, ClientAddress address) {
    assertThat(addressDot).isNotNull();
    assertThat(addressDot.client).isEqualTo(address.client);
    assertThat(addressDot.type).isEqualTo(address.type);
    assertThat(addressDot.street).isEqualTo(address.street);
    assertThat(addressDot.house).isEqualTo(address.house);
    assertThat(addressDot.flat).isEqualTo(address.flat);
  }

  private ClientPhoneDot getClientPhoneDot(Integer id, PhoneType type) {
    return clientTestDao.get().getClientPhoneDot(id, type);
  }

  private ClientAddressDot getClientAddressDot(Integer id, AddressTypeEnum type) {
    return clientTestDao.get().getClientAddressDot(id, type);
  }

  private ClientDot getClientDot(Integer id) {
    return clientTestDao.get().getClientDot(id);
  }

  @Test
  public void editClient() {
    Integer clientId = RND.plusInt(Integer.MAX_VALUE);
    ClientToSave clientToSave = generateRandomClientToSave(clientId);

    {
      insertClient(generateRandomClientDetails(clientId));
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

  @Test
  public void deleteClient() {

    Integer rClientId = RND.plusInt(Integer.MAX_VALUE);

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

  enum PaginationEnum {
    LimitLessThanOffset, OffsetLessThanLimit, LimitOffsetEqual
  }

  @DataProvider
  public Object[][] getRecords_pagination_DP() {
    return new Object[][]{
      new Object[]{PaginationEnum.LimitLessThanOffset},
      new Object[]{PaginationEnum.LimitOffsetEqual},
      new Object[]{PaginationEnum.OffsetLessThanLimit}
    };
  }

  //FIXME разделить на 5 тестов. Подойди на третьий объясню
  @Test(dataProvider = "getRecords_pagination_DP")
  public void getRecords_pagination(PaginationEnum paginationEnum) {

    int offset = 0, limit = 0;
    switch (paginationEnum) {
      case LimitLessThanOffset:
        limit = RND.plusInt(100);
        offset = RND.plusInt(100) + limit;
        break;
      case LimitOffsetEqual:
        limit = RND.plusInt(100);
        offset = limit;
        break;
      case OffsetLessThanLimit:
        offset = RND.plusInt(100);
        limit = RND.plusInt(100) + offset;
    }

    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = offset;
    emptyFilter.limit = limit;

    List<ClientDot> leftDots = new ArrayList<>();

    {
      for (int i = 0; i < offset + limit; i++) {
        ClientDot dot = generateRandomClientDot();
        leftDots.add(dot);
        insertClient(dot);
      }

      Comparator<ClientDot> comparator = Comparator.comparing(o -> o.id);
      leftDots.sort(comparator);
    }

    //
    //
    //
    List<ClientRecord> clientRecordList = clientRegister.get().getRecords(emptyFilter);
    //
    //
    //

    assertThat(clientRecordList.size()).isEqualTo(limit);

    for (int i = 0; i < limit; i++)
      isEqual(clientRecordList.get(i), leftDots.get(i + offset));
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
  public void getRecordsWithFilter(FioEnum fioEnum) {

    String rText = RND.str(5);

    ClientFilter filter = new ClientFilter();
    filter.fio = rText;

    {
      for (int i = 0; i < 20; i++) {
        insertClient(generateRandomClientDot());
      }
    }

    ClientDot dot;

    {
      dot = generateRandomClientDot();

      switch (fioEnum) {
        case SURNAME:
          dot.surname = rText;
          break;
        case NAME:
          dot.name = rText;
          break;
        case PATRONYMIC:
          dot.patronymic = rText;
      }

      insertClient(dot);
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

    ClientRecord expected = fromDot(dot);
    // FIXME: 7/18/18 Сделай отдельный тест, который проверяет все поля. а во всех остальных местах  проверяй только ID
    //assertThat(clientRecordList.get(0)).isEqualsToByComparingFields(expected);
    assertThat(clientRecordList.get(0).id).isEqualTo(expected.id);
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
  public void getRecords_WithSort(SortByEnum sortByEnum, SortDirection sortDirection) {
    ClientFilter filter = new ClientFilter();
    filter.sortByEnum = sortByEnum;
    filter.sortDirection = sortDirection;

    List<ClientRecord> clientRecords = Lists.newArrayList();

    {
      for (int i = 0; i < 20; i++) {

        ClientDot dot = generateRandomClientDot();
        insertClient(dot);

        generateRandomAccountsFor(dot.id, RND.plusInt(50));

        clientRecords.add(fromDot(dot));
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
  public void getRecordsCountWithEmptyFilter() {

    int randomCount = RND.plusInt(40);

    ClientFilter filter = new ClientFilter();

    {
      for (int i = 0; i < randomCount; i++) {
        Integer clientId = RND.plusInt(Integer.MAX_VALUE);
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
  public void getRecordsCountWithFilter(FioEnum fioEnum) {

    int randomCount = RND.plusInt(10);
    String rFio = RND.str(10);

    ClientFilter filter = new ClientFilter();
    filter.fio = rFio;

    {
      for (int i = 0; i < RND.plusInt(40); i++) {
        Integer clientId = RND.plusInt(Integer.MAX_VALUE);
        ClientDetails details = generateRandomClientDetails(clientId);
        insertClient(details);
      }
    }

    {
      for (int i = 0; i < randomCount; i++) {
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

  @Test
  public void getCharms() {
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
      isEqual(resultCharmRecords.get(i), charmRecords.get(i));
  }

  private void isEqual(CharmRecord cr1, CharmRecord cr2) {
    assertThat(cr1.id).isEqualTo(cr2.id);
    assertThat(cr1.energy).isEqualTo(cr2.energy);
    assertThat(cr1.description).isEqualTo(cr2.description);
    assertThat(cr1.name).isEqualTo(cr2.name);
  }

  class TestRender implements ClientRender {

    private String name;
    private List<ClientRecord> clientRows;

    TestRender() {
      clientRows = Lists.newArrayList();
    }

    @Override
    public void start(String name, Date contractDate) {
      this.name = name;
    }

    @Override
    public void append(ClientRecord asdRow) {
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

    List<ClientDot> leftClientDots = Lists.newArrayList();

    Integer randomCount = RND.plusInt(100) + 100;

    {
      for (int i = 0; i < randomCount; i++) {
        ClientDot dot = generateRandomClientDot();
        switch (fioEnum) {
          case SURNAME:
            dot.surname = RND.str(10) + rFio + RND.str(10);
            break;
          case NAME:
            dot.name = RND.str(10) + rFio + RND.str(10);
            break;
          case PATRONYMIC:
            dot.patronymic = RND.str(10) + rFio + RND.str(10);
        }
        leftClientDots.add(dot);
        insertClient(dot);
        generateRandomAccountsFor(dot.id, RND.plusInt(50));
      }
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
    assertThat(render.clientRows).hasSize(randomCount);
    for (ClientDot clientDot : leftClientDots)
      isEqual(render.clientRows.get(0), clientDot);
  }

  private void isEqual(ClientRecord clientRecord, ClientDot clientDot) {
    assertThat(clientRecord).isEqualsToByComparingFields(fromDot(clientDot));
  }

  private List<ClientDetails> getClientDetailsList() {
    List<ClientDetails> clientDetails = Lists.newArrayList();
    for (Client client : clientTestDao.get().getClients())
      clientDetails.add(fromClient(client));
    return clientDetails;
  }

  private ClientDetails fromClient(Client client) {
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

  private ClientRecord fromDot(ClientDot dot) {
    ClientRecord record = new ClientRecord();
    record.id = dot.id;
    record.surname = dot.surname;
    record.name = dot.name;
    record.patronymic = dot.patronymic;
    record.age = getAge(dot.birthDate);
    List<ClientAccount> clientAccounts = clientTestDao.get().getClientAccounts(dot.id);
    record.middle_balance = getMiddleBalance(clientAccounts);
    record.max_balance = getMaxBalance(clientAccounts);
    record.min_balance = getMinBalance(clientAccounts);
    return record;
  }

  private int getAge(Date dateOfBirth) {
    LocalDate birthDateLocal = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date current = new Date();
    LocalDate currentDate = current.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return calculateAge(birthDateLocal, currentDate);
  }

  private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
    if ((birthDate != null) && (currentDate != null)) {
      return Period.between(birthDate, currentDate).getYears();
    } else {
      return 0;
    }
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
    double middle_balance = 0;
    if (clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      middle_balance += clientAccount.money;
    return (float) (middle_balance / (float) clientAccounts.size());
  }

  private void insertClient(ClientDetails details) {
    CharmRecord charmRecord = new CharmRecord(details.charmId, RND.str(10), RND.str(10), (float) RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charmId);
    insertClientAddress(details.addressFact);
    insertClientAddress(details.addressReg);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);
  }

  private void insertClient(ClientDot clientDot) {
    CharmRecord charmRecord = new CharmRecord(clientDot.charmId, RND.str(10), RND.str(10), (float) RND.plusDouble(Double.MAX_VALUE, 0));
    insertCharm(charmRecord);
    clientTestDao.get().insertClient(clientDot.id, clientDot.surname, clientDot.name, clientDot.patronymic, clientDot.gender, clientDot.birthDate, clientDot.charmId);
  }

  private ClientToSave generateRandomClientToSave(Integer id) {
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

  private ClientDot generateRandomClientDot() {
    ClientDot clientDot = new ClientDot();
    clientDot.id = RND.plusInt(Integer.MAX_VALUE);
    clientDot.surname = RND.str(10);
    clientDot.name = RND.str(10);
    clientDot.patronymic = RND.str(10);
    Calendar c = new GregorianCalendar();
    c.add(Calendar.YEAR, RND.plusInt(2018));
    clientDot.birthDate = c.getTime();
    clientDot.charmId = RND.plusInt(Integer.MAX_VALUE);
    clientDot.gender = GenderEnum.MALE;
    return clientDot;
  }

  private ClientDetails generateRandomClientDetails(Integer id) {
    ClientDetails details = new ClientDetails();
    details.id = id;
    details.surname = RND.intStr(10);
    details.name = RND.intStr(10);
    details.patronymic = RND.intStr(10);
    details.birthDate = RND.dateYears(-2018, 0);
    details.gender = GenderEnum.MALE;
    details.charmId = RND.plusInt(Integer.MAX_VALUE);
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    return details;
  }

  private void generateRandomAccountsFor(Integer id, int i) {
    for (int c = 0; c < i; c++) {
      ClientAccount clientAccount = new ClientAccount();
      clientAccount.client = id;
      clientAccount.number = RND.intStr(11);
      clientAccount.money = (float) RND.plusDouble(3000, 10);
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

  private void insertCharm(CharmRecord charmRecord) {
    clientTestDao.get().insertCharm(charmRecord.name, charmRecord.description, charmRecord.energy);
  }

  private static void sortList(List<ClientRecord> clientRecords, SortByEnum sortBy, SortDirection sortDirection) {
    Comparator<ClientRecord> comparator = null;
    switch (sortBy) {
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