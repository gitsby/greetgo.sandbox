package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @BeforeMethod
  public void clearDB() throws Exception {
    clientTestDao.get().clearAllTables();
  }

  @Test
  public void getDetail() throws Exception {
    Integer clientId = RND.plusInt(100);
    Details details;

    {
      details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    //
    //
    //
    Details resultDetail = clientRegister.get().detail(clientId);
    //
    //
    //

    assertThat(resultDetail).isNotNull();
    assertThat(resultDetail.surname).isEqualTo(details.surname);
    assertThat(resultDetail.charm.id).isEqualTo(details.charm.id);
    assertThat(resultDetail.addressFact.client).isEqualTo(details.addressFact.client);
    assertThat(resultDetail.homePhone.client).isEqualTo(details.homePhone.client);
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

    {
      int count = clientTestDao.get().count();
      assertThat(count).isEqualTo(1);
    }
  }

  @Test
  public void editClient() throws Exception {
    Integer clientId = RND.plusInt(100);
    ClientToSave clientToSave = generateRandomClientToSave(clientId);

    {
      Details leftClient = generateRandomClientDetails(clientId);
      insertClient(leftClient);
    }


    //
    //
    //
    clientRegister.get().save(clientToSave);
    //
    //
    //

    {
      int count = clientTestDao.get().count();
      assertThat(count).isEqualTo(1);
    }

    {
      String actualName = clientTestDao.get().loadParamValue(clientId, "name");
      ClientAddress actualAddressReg = clientTestDao.get().getAddress(clientId, AddressTypeEnum.REG);
      assertThat(actualName).isEqualTo(clientToSave.name);
      assertThat(actualAddressReg.client).isEqualTo(clientToSave.addressReg.client);
    }
  }

  @Test
  public void deleteClient() throws Exception {

    Integer clientId = RND.plusInt(100);
    Details details = generateRandomClientDetails(clientId);
    insertClient(details);


    //
    //
    //
    clientRegister.get().delete(clientId);
    //
    //
    //

    {
      int count = clientTestDao.get().count();
      assertThat(count).isZero();
    }
  }


  @Test
  public void getRecordsWithEmptyFilter() throws Exception  {

    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = 0;
    emptyFilter.limit = 10;

    for (int i = 0; i < 40; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
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

  @Test
  public void getRecordsWithFilter() throws Exception  {

    String rName= RND.str(5);
    ClientFilter filter = new ClientFilter();
    filter.fio = rName;

    for (int i = 0; i < 20; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    Integer clientId = (int)(System.nanoTime()/10000);
    Details details = generateRandomClientDetails(clientId);
    details.name = rName + RND.str(10);
    insertClient(details);

    //
    //
    //
    List<ClientRecord> clientRecordList = clientRegister.get().getRecords(filter);
    //
    //
    //

    assertThat(clientRecordList.size()).isEqualTo(1);
  }

  @Test
  public void getRecordsWithSort() throws Exception {
    ClientFilter filter = new ClientFilter();
    filter.sortByEnum = SortByEnum.FULL_NAME;

    List<ClientRecord> clientRecords = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
      generateRandomAccountsFor(details.id, RND.plusInt(50));
      clientRecords.add(getRecordsFromDetails(details));
    }

    sortList(clientRecords, filter.sortByEnum);


    //
    //
    //
    List<ClientRecord> result = clientRegister.get().getRecords(filter);
    //
    //
    //

    assertThat(result.size()).isEqualTo(clientRecords.size());
    for(int i=0; i<result.size(); i++)
      assertThat(clientRecords.get(i).id).isEqualTo(result.get(i).id);
  }

  @Test
  public void getRecordsCountWithEmptyFilter() throws Exception  {

    int randomCount = RND.plusInt(40);

    ClientFilter filter = new ClientFilter();

    for (int i = 0; i < randomCount; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(filter);
    //
    //
    //

    assertThat(count).isEqualTo(randomCount);
  }

  @Test
  public void getRecordsCountWithFilter() throws Exception  {

    int randomCount = RND.plusInt(40);
    String randomFio = RND.str(10);

    ClientFilter filter = new ClientFilter();
    filter.fio = randomFio;

    for (int i = 0; i < randomCount; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      details.name = randomFio;
      insertClient(details);
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(filter);
    //
    //
    //

    assertThat(count).isEqualTo(1);
  }

  @Test
  public void getCharms() throws Exception {
    int randomCount = RND.plusInt(100);
    List<CharmRecord> insertedCharmRecords = new ArrayList<>();

    {
      for (int i = 0; i < randomCount; i++) {
        CharmRecord charmRecord = new CharmRecord((int) (System.nanoTime() / 10000), RND.str(10), RND.str(10), RND.rnd.nextFloat());
        insertCharm(charmRecord);
        insertedCharmRecords.add(charmRecord);
      }
    }

    //
    //
    //
    List<CharmRecord> charmRecords = clientRegister.get().getCharms();
    //
    //
    //

    assertThat(charmRecords.size()).isEqualTo(randomCount);
    for (int i=0; i<charmRecords.size(); i++)
      assertThat(charmRecords.get(i).id).isEqualTo(insertedCharmRecords.get(i).id);

  }

  private static class TestRender implements ClientRender {

    private String name;
    private Date contractDate;
    private List<ClientRow> asdRows;
    private String authorName;

    public TestRender() {
      asdRows = Lists.newArrayList();
    }

    @Override
    public void start(String name, Date contractDate) {
      this.name = name;
      this.contractDate = contractDate;
    }

    @Override
    public void append(ClientRow asdRow) {
      this.asdRows.add(asdRow);
    }

    @Override
    public void finish(String authorName) {
      this.authorName = authorName;
    }
  }

  @Test
  public void renderClientList() {
    TestRender render = new TestRender();

    String name = RND.str(10);
    String authorName = RND.str(10);
    Details leftDetails;

    {
      leftDetails = generateRandomClientDetails(RND.plusInt(10));
      insertClient(leftDetails);
    }

    //
    //
    //
    clientRegister.get().renderClientList(name, authorName, render);
    //
    //
    //

    assertThat(render.asdRows).hasSize(1);
    assertThat(render.asdRows.get(0).id).isEqualTo(leftDetails.id);
    assertThat(render.name).isEqualTo(name);
    assertThat(render.authorName).isEqualTo(authorName);
  }


  private ClientRecord getRecordsFromDetails(Details details) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = details.id;
    clientRecord.surname = details.surname;
    clientRecord.name = details.name;
    clientRecord.patronymic = details.patronymic;
    List<ClientAccount> clientAccounts = clientTestDao.get().getClientAccounts(details.id);
    clientRecord.middle_balance = getMiddleBalance(clientAccounts);
    clientRecord.max_balance = getMaxBalance(clientAccounts);
    clientRecord.min_balance = getMinBalance(clientAccounts);
    return clientRecord;
  }

  private float getMinBalance(List<ClientAccount> clientAccounts) {
    float min_balance = Integer.MAX_VALUE;
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money < min_balance) min_balance = clientAccount.money;
    return min_balance;
  }

  private float getMaxBalance(List<ClientAccount> clientAccounts) {
    float max_balance = -1;
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      if (clientAccount.money > max_balance) max_balance = clientAccount.money;
    return max_balance;
  }

  private float getMiddleBalance(List<ClientAccount> clientAccounts) {
    float middle_balance = 0;
    if(clientAccounts.size() == 0) return 0;
    for (ClientAccount clientAccount : clientAccounts)
      middle_balance += clientAccount.money;
    return middle_balance / clientAccounts.size();
  }

  private void insertClient(Details details) {
    insertCharm(details.charm);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charm.id);
    insertClientAddress(details.addressFact);
    insertClientAddress(details.addressReg);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);
  }

  private ClientToSave generateRandomClientToSave(Integer id) {
    Details details = generateRandomClientDetails(id);
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.id = details.id;
    clientToSave.surname = details.surname;
    clientToSave.name = details.name;
    clientToSave.patronymic = details.patronymic;
    clientToSave.birthDate = details.birthDate;
    clientToSave.gender = details.gender;
    clientToSave.charmId = details.charm.id;
    clientToSave.addressFact = details.addressFact;
    clientToSave.addressReg = details.addressReg;
    clientToSave.homePhone = details.homePhone;
    clientToSave.mobilePhone = details.mobilePhone;
    clientToSave.workPhone = details.workPhone;
    insertCharm(details.charm);
    return clientToSave;
  }

  private Details generateRandomClientDetails(Integer id) {
    Details details = new Details();
    details.id = id;
    details.surname = RND.str(10);
    details.name = RND.str(10);
    details.patronymic = RND.str(10);
    details.birthDate = new Date();
    details.gender = Gender.MALE;
    details.charm = new CharmRecord((int)(System.nanoTime()/100000), RND.str(10), RND.str(10), RND.rnd.nextFloat());
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    return details;
  }

  private void generateRandomAccountsFor(Integer id, int i) {
    for (int c=0; c<i; c++) {
      ClientAccount clientAccount = new ClientAccount();
      clientAccount.client = id;
      clientAccount.number = RND.intStr(11);
      clientAccount.money = (float)RND.plusDouble(10000, 3);
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
    clientTestDao.get().insertCharm(charmRecord.id, charmRecord.name, charmRecord.description, charmRecord.energy);
  }

  private void sortList(List<ClientRecord> clientRecords, SortByEnum sortBy) {
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
  }
}
