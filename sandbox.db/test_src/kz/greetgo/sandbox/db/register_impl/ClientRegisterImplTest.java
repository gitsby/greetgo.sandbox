package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  @Test
  public void firstTest() throws Exception {
    System.out.println("TEST");
    assertThat(clientRegister.get().getCharacters()).isNull();
  }

  @Test
  public void checkCharactersNotNull(){
    assertThat(clientRegister.get().getCharacters()).isNotNull();
  }

  @Test
  public void getClientWithNullId(){

  }

}
