package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void getDetail() throws Exception {
    Integer clientId = RND.plusInt(100);
    Details details = generateRandomClientDetails(clientId);
    insertClient(details);

    //
    //
    //
    Details resultDetail = clientRegister.get().detail(clientId);
    //
    //
    //

    assertThat(resultDetail).isNotNull();
    assertThat(resultDetail.surname).isEqualTo(details.surname);
    assertThat(resultDetail.charmRecord.id).isEqualTo(details.charmRecord.id);
    assertThat(resultDetail.addressFact.client).isEqualTo(details.addressFact.client);
    assertThat(resultDetail.homePhone.client).isEqualTo(details.homePhone.client);
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

    {
      int count = clientTestDao.get().count();
      assertThat(count).isEqualTo(1);
    }
  }

  @Test
  public void editClient() {
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
      assertThat(actualAddressReg).isEqualTo(clientToSave.addressReg);
    }
  }

  @Test
  public void deleteClient() {

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
  public void getRecordsWithEmptyFilter() {

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
  public void getRecordsWithFilter() {

    String rName= RND.str(5);
    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = 0;
    emptyFilter.limit = 10;
    emptyFilter.fio = rName;

    for (int i = 0; i < 20; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    Integer clientId = (int)(System.nanoTime()/10000);
    Details details = generateRandomClientDetails(clientId);
    details.name = rName;
    insertClient(details);

    //
    //
    //
    List<ClientRecord> clientRecordList = clientRegister.get().getRecords(emptyFilter);
    //
    //
    //

    assertThat(clientRecordList.size()).isEqualTo(1);
  }

  @Test
  public void getRecordsCountWithEmptyFilter() {

    int randomLimit = RND.plusInt(40);

    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = 0;
    emptyFilter.limit = randomLimit;

    for (int i = 0; i < 40; i++) {
      Integer clientId = (int)(System.nanoTime()/10000);
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(emptyFilter);
    //
    //
    //

    assertThat(count).isEqualTo(randomLimit);
  }

  @Test
  public void getRecordsCountWithFilter() {

    int randomLimit = RND.plusInt(40);

    ClientFilter emptyFilter = new ClientFilter();
    emptyFilter.offset = 0;
    emptyFilter.limit = randomLimit;

    for (int i = 20; i < 40; i++) {
      Integer clientId = i;
      Details details = generateRandomClientDetails(clientId);
      insertClient(details);
    }

    //
    //
    //
    Integer count = clientRegister.get().getRecordsCount(emptyFilter);
    //
    //
    //

    assertThat(count).isEqualTo(randomLimit);
  }

  private void insertClient(Details details) {
    insertCharm(details.charmRecord);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charmRecord.id);
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
    clientToSave.charmId = details.charmRecord.id;
    clientToSave.addressFact = details.addressFact;
    clientToSave.addressReg = details.addressReg;
    clientToSave.homePhone = details.homePhone;
    clientToSave.mobilePhone = details.mobilePhone;
    clientToSave.workPhone = details.workPhone;
    insertCharm(details.charmRecord);
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
    long r = System.nanoTime();
    details.charmRecord = new CharmRecord((int)(r/100000), RND.str(10), RND.str(10), RND.rnd.nextFloat());
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    return details;
  }

  private void insertClientAddress(ClientAddress clientAddress) {
    clientTestDao.get().insertClientAddress(clientAddress.client, clientAddress.type, clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private void insertClientPhone(ClientPhone clientPhone) {
    clientTestDao.get().insertClientPhone(clientPhone.client, clientPhone.number, clientPhone.type);
  }

  private void insertCharm(CharmRecord charmRecord) {
    clientTestDao.get().insertCharm(charmRecord.id, charmRecord.name, charmRecord.description, charmRecord.energy);
  }
}
