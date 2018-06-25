package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDaoHelper;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDaoHelper> clientTestDaoHelper;

  @BeforeMethod
  public void clearDB() {
    clientTestDaoHelper.get().clearAllTables();
  }

  @Test
  public void getDetail() throws Exception {
    Integer clientId = RND.plusInt(100);
    ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);

    {
      clientTestDaoHelper.get().insertClient(details);
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
    ClientToSave clientToSave = clientTestDaoHelper.get().generateRandomClientToSave(null);

    //
    //
    //
    clientRegister.get().save(clientToSave);
    //
    //
    //

    List<ClientDetails> detailsList;

    {
      detailsList = clientTestDaoHelper.get().getClientDetailsList();
      assertThat(detailsList).isNotNull();
      assertThat(detailsList).hasSize(1);
    }

    ClientDetails details = detailsList.get(0);
    isEqual(details, clientToSave);
  }

  @Test
  public void editClient() throws Exception {
    Integer clientId = RND.plusInt(100);
    ClientToSave clientToSave = clientTestDaoHelper.get().generateRandomClientToSave(clientId);

    {
      ClientDetails leftClient = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
      clientTestDaoHelper.get().insertClient(leftClient);
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
      detailsList = clientTestDaoHelper.get().getClientDetailsList();
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
      ClientDot leftDot = clientTestDaoHelper.get().generateRandomClientDot();
      leftDot.id = rClientId;
      clientTestDaoHelper.get().insertClient(leftDot);
    }

    //
    //
    //
    clientRegister.get().delete(rClientId);
    //
    //
    //

    {
      Integer actual = clientTestDaoHelper.get().getActual(rClientId);
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
        ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
        clientTestDaoHelper.get().insertClient(details);
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
    return new Object[][] {
      new Object[] {FioEnum.SURNAME},
      new Object[] {FioEnum.NAME},
      new Object[] {FioEnum.PATRONYMIC}
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
        ClientDetails leftDetails = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
        clientTestDaoHelper.get().insertClient(leftDetails);
      }
    }

    ClientDetails details;

    {
      Integer clientId = RND.plusInt(10000);
      details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);

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

      clientTestDaoHelper.get().insertClient(details);
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
    return new Object[][] {
      new Object[] {SortByEnum.FULL_NAME, SortDirection.ASCENDING},
      new Object[] {SortByEnum.AGE, SortDirection.ASCENDING},
      new Object[] {SortByEnum.MIDDLE_BALANCE, SortDirection.ASCENDING},
      new Object[] {SortByEnum.MAX_BALANCE, SortDirection.ASCENDING},
      new Object[] {SortByEnum.MIN_BALANCE, SortDirection.ASCENDING},
      new Object[] {SortByEnum.FULL_NAME, SortDirection.DESCENDING},
      new Object[] {SortByEnum.AGE, SortDirection.DESCENDING},
      new Object[] {SortByEnum.MIDDLE_BALANCE, SortDirection.DESCENDING},
      new Object[] {SortByEnum.MAX_BALANCE, SortDirection.DESCENDING},
      new Object[] {SortByEnum.MIN_BALANCE, SortDirection.DESCENDING}
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

        ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
        clientTestDaoHelper.get().insertClient(details);

        clientTestDaoHelper.get().generateRandomAccountsFor(details.id, RND.plusInt(50));

        clientRecords.add(clientTestDaoHelper.get().getRecordsFromDetails(details));
      }
    }

    clientTestDaoHelper.get().sortList(clientRecords, sortByEnum, sortDirection);


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
        ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
        clientTestDaoHelper.get().insertClient(details);
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
    String rFio= RND.str(10);

    ClientFilter filter = new ClientFilter();
    filter.fio = rFio;

    {
      for (int i = 0; i < randomCount; i++) {
        Integer clientId = RND.plusInt(Integer.MAX_VALUE);
        ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
        clientTestDaoHelper.get().insertClient(details);
      }
    }

    {
      Integer clientId = RND.plusInt(Integer.MAX_VALUE);
      ClientDetails details = clientTestDaoHelper.get().generateRandomClientDetails(clientId);
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
      clientTestDaoHelper.get().insertClient(details);
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
        clientTestDaoHelper.get().insertCharm(charmRecord);
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
    public void finish() {}
  }

  @Test
  public void renderClientList() {
    TestRender render = new TestRender();

    String name = RND.str(10);
    ClientDetails leftDetails;

    ClientFilter filter = new ClientFilter();

    {
      leftDetails = clientTestDaoHelper.get().generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE));
      clientTestDaoHelper.get().insertClient(leftDetails);
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
      leftDetails = clientTestDaoHelper.get().generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE));
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
      clientTestDaoHelper.get().insertClient(leftDetails);
    }

    {
      for (int i = 0; i < 10; i++)
        clientTestDaoHelper.get().insertClient(clientTestDaoHelper.get().generateRandomClientDetails(RND.plusInt(Integer.MAX_VALUE)));
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

}
