package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Набор автоматизированных тестов для тестирования методов класса {@link ClientRegisterImpl}
 */
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> clientTestDao;


  @Test
  public void getClientRecordsCount_withFilter_NotFound() throws Exception {
    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));
    insertClient(rClient);

    ClientFilter clientFilter = new ClientFilter(null, null, null, null, RND.str(10));

    //
    //
    //
    int count = clientRegister.get().getRecordsCount(clientFilter);
    //
    //
    //

    assertThat(count).isEqualTo(0);
  }

  @Test
  public void getClientRecordsCount_withFilter_ok() throws Exception {

    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));
    String name = rClient.name;
    insertClient(rClient);

    ClientFilter clientFilter = new ClientFilter(null, null, null, null, name);

    //
    //
    //
    int count = clientRegister.get().getRecordsCount(clientFilter);
    //
    //
    //

    assertThat(count).isEqualTo(1);
  }

  @Test
  public void getClientRecordsCount_withoutFilter_ok() throws Exception {

    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));

    insertClient(rClient);

    //
    //
    //
    int count = clientRegister.get().getRecordsCount(null);
    //
    //
    //

    assertThat(count).isEqualTo(1);
  }



  @Test
  public void update_client_ok() throws Exception {
    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));

    insertClient(rClient);

    String newName = RND.str(10);
    clientTestDao.get().updateClientField(rClient.id, "name", newName);

    //
    //
    //
    ClientInfo clientInfo = clientRegister.get().get(rClient.id);
    //
    //
    //

    assertThat(clientInfo).isNotNull();
    assertThat(clientInfo.name).isEqualTo(newName);
  }


  @Test
  public void insert_client__ok() throws Exception {

    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));
    insertClient(rClient);

    //
    //
    //
    ClientInfo clientInfo = clientRegister.get().get(rClient.id);
    //
    //
    //

    assertThat(clientInfo).isNotNull();
    assertThat(clientInfo.name).isEqualTo(rClient.name);
    assertThat(clientInfo.surname).isEqualTo(rClient.surname);
    assertThat(clientInfo.patronymic).isEqualTo(rClient.patronymic);
    assertThat(clientInfo.gender).isEqualTo(rClient.gender);
    assertThat(clientInfo.addressRegId).isEqualTo(rClient.addressReg.id);
    assertThat(clientInfo.addressFactId).isEqualTo(rClient.addressFact.id);
    assertThat(clientInfo.homePhoneId).isEqualTo(rClient.homePhone.id);
    assertThat(clientInfo.workPhoneId).isEqualTo(rClient.workPhone.id);
    assertThat(clientInfo.mobilePhoneId).isEqualTo(rClient.mobilePhone.id);
  }

  @Test
  public void remove_client_ok() throws Exception {

    ClientDetail rClient = getRandomClientDetail(RND.plusInt(100));
    insertClient(rClient);

    //
    //
    //
    clientRegister.get().remove(rClient.id);
    //
    //
    //


  }



  private ClientDetail getRandomClientDetail(Integer id) {
    Integer client_id = id;
    String name = RND.str(10);
    String surname = RND.str(10);
    String patronymic = RND.str(10);
    Gender gender = Gender.MALE;
    Date birth_day = new Date();

    Charm charm = new Charm(id, RND.str(10), RND.str(10), (float)RND.plusDouble(12, 1));

    ClientAddress addressReg = new ClientAddress(id, AddressType.REG, RND.str(10), RND.str(10), RND.str(10));
    ClientAddress addressFact = new ClientAddress(id, AddressType.FACT, RND.str(10), RND.str(10), RND.str(10));

    ClientPhone homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    ClientPhone workPhone = new ClientPhone(id, PhoneType.WORK, RND.intStr(11));
    ClientPhone mobilePhone = new ClientPhone(id, PhoneType.MOBILE, RND.intStr(11));

    return new ClientDetail(client_id, surname, name, patronymic, gender, birth_day, charm, addressReg, addressFact, homePhone, workPhone, mobilePhone);
  }

  private void insertClient(ClientDetail clientDetail) {
    clientTestDao.get().insertClientAddress(clientDetail.addressReg.id, clientDetail.addressReg.type, clientDetail.addressReg.street, clientDetail.addressReg.house, clientDetail.addressReg.flat);
    clientTestDao.get().insertClientAddress(clientDetail.addressFact.id, clientDetail.addressFact.type, clientDetail.addressFact.street, clientDetail.addressFact.house, clientDetail.addressFact.flat);
    clientTestDao.get().insertClientPhone(clientDetail.homePhone.id, clientDetail.homePhone.type, clientDetail.homePhone.number);
    clientTestDao.get().insertClientPhone(clientDetail.workPhone.id, clientDetail.workPhone.type, clientDetail.workPhone.number);
    clientTestDao.get().insertClientPhone(clientDetail.mobilePhone.id, clientDetail.mobilePhone.type, clientDetail.mobilePhone.number);
    clientTestDao.get().insertClient(clientDetail.id, clientDetail.surname, clientDetail.name, clientDetail.patronymic, clientDetail.gender, clientDetail.birth_day, clientDetail.addressReg.id, clientDetail.addressFact.id, clientDetail.homePhone.id, clientDetail.workPhone.id, clientDetail.mobilePhone.id);
  }
}
