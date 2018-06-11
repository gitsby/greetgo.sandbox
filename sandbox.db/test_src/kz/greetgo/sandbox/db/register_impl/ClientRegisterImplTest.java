package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.model.Gender;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.junit.Test;

import java.util.Date;


public class ClientRegisterImplTest extends ParentTestNg {

  private BeanGetter<ClientRegister> clientRegister;

  private BeanGetter<ClientTestDao> clientTestDao;


  @Test
  public void getClientDetail_ok() throws Exception {
    Integer client_id = RND.plusInt(10);
    String name = RND.str(10);
    String surname = RND.str(10);
    String patronymic = RND.str(10);
    Gender gender = Gender.MALE;
    Date birth_day = new Date();
    Integer addressRegId = RND.plusInt(10);
    Integer addressFactId = RND.plusInt(10);
    Integer homePhoneId = RND.plusInt(10);
    Integer workPhoneId = RND.plusInt(10);
    Integer mobilePhoneId = RND.plusInt(10);

    clientTestDao.get().insertClient(client_id, name, surname, patronymic, gender, birth_day, addressRegId, addressFactId, homePhoneId, workPhoneId, mobilePhoneId);


    //
    //
    //
    //
    //ClientInfo clientInfo = clientRegister.get().get(client_id);
    //
    //
    //

  }
}
