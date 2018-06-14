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
  public void getDetail() throws Exception {
    Integer clientId = RND.plusInt(100);
    Details details = generateRandomClientDetails(clientId);
    insertCharm(details.charm);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, details.patronymic, details.gender, details.birthDate, details.charm.id);
    insertClientAddress(details.addressFact);
    insertClientAddress(details.addressReg);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);

    //
    //
    //
    Details resultDetail = clientRegister.get().detail(clientId);
    //
    //
    //

    assertThat(resultDetail).isNotNull();
    assertThat(resultDetail.surname).isEqualTo(details.surname);
    assertThat(resultDetail.addressFact.client).isEqualTo(details.addressFact.client);
    assertThat(resultDetail.homePhone.client).isEqualTo(details.homePhone.client);
  }

  @Test
  public void insertClient_withoutMandatoryValues() {
    Details details = generateRandomClientDetails(1);
    insertCharm(details.charm);
    clientTestDao.get().insertClient(details.id, details.surname, details.name, null, details.gender, details.birthDate, details.charm.id);
    insertClientAddress(details.addressFact);
    insertClientPhone(details.homePhone);
    insertClientPhone(details.workPhone);
    insertClientPhone(details.mobilePhone);

    //
    //
    //
    Details resultDetail = clientRegister.get().detail(1);
    //
    //
    //

    assertThat(resultDetail).isNotNull();
    assertThat(resultDetail.surname).isEqualTo(details.surname);
    assertThat(resultDetail.addressFact.client).isEqualTo(details.addressFact.client);
    assertThat(resultDetail.homePhone.client).isEqualTo(details.homePhone.client);
  }

  private Details generateRandomClientDetails(Integer id) {
    Details details = new Details();
    details.id = id;
    details.surname = RND.str(10);
    details.name = RND.str(10);
    details.patronymic = RND.str(10);
    details.birthDate = new Date();
    details.gender = Gender.MALE;
    details.charm = new Charm(RND.plusInt(100), RND.str(10), RND.str(10), RND.rnd.nextFloat());
    details.addressFact = new ClientAddress(id, AddressTypeEnum.FACT, RND.str(10), RND.str(10), RND.str(10));
    details.addressReg = new ClientAddress(id, AddressTypeEnum.REG, RND.str(10), RND.str(10), RND.str(10));
    details.homePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.mobilePhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    details.workPhone = new ClientPhone(id, PhoneType.HOME, RND.intStr(11));
    return details;
  }


  private void insertClientAddress(ClientAddress clientAddress) {
    clientTestDao.get().insertClientAddress(clientAddress.client, clientAddress.type, clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private void insertClientPhone(ClientPhone clientPhone) {
    clientTestDao.get().insertClientPhone(clientPhone.client, clientPhone.number, clientPhone.type);
  }

  private void insertCharm(Charm charm) {
    clientTestDao.get().insertCharm(charm.id, charm.name, charm.description, charm.energy);
  }
}
