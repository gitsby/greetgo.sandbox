package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordPhilter;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;

  public BeanGetter<ClientTestDao> testDaoBeanGetter;

  @Test
  public void checkCharactersNotNull() {
    assertThat(clientRegister.get().getCharacters()).isNotNull();
  }

  @Test
  public void getClientWithNullId() {
    int clientId = -1;
    assertThat(clientRegister.get().getClientById(clientId)).isNull();
  }

  @Test
  public void searchForEmptyName() {

    ClientRecordPhilter clientRecordPhilter = new ClientRecordPhilter();
    clientRecordPhilter.columnName = "empty";
    clientRecordPhilter.paginationPage = 0;
    clientRecordPhilter.sliceNum = 10;
    clientRecordPhilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordPhilter));

  }

  @Test
  public void tooBigSliceNum() {
    ClientRecordPhilter clientRecordPhilter = new ClientRecordPhilter();
    clientRecordPhilter.columnName = "empty";
    clientRecordPhilter.paginationPage = 0;
    clientRecordPhilter.sliceNum = 1000000000;
    clientRecordPhilter.searchName = "";

    assertThat(clientRegister.get().getClients(clientRecordPhilter));
  }

  @Test
  public void getNonExistingPaginationPage() {
    ClientRecordPhilter clientRecordPhilter = new ClientRecordPhilter();
    clientRecordPhilter.columnName = "empty";
    clientRecordPhilter.paginationPage = 900;
    clientRecordPhilter.sliceNum = 10;
    clientRecordPhilter.searchName = "";

    assertThat(clientRegister.get().getClients(clientRecordPhilter));
  }


  @Test
  public void testCreateClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.id=-1;

    clientToSave.charm = 1;
    // Send new client

    assertThat(clientRegister.get().editedClient(clientToSave));
  }

  @Test
  public void testCreateClientWithNotExistingCharacter(){

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.id=null;

    clientToSave.charm = -1;
    // Send new client

    assertThat(clientRegister.get().editedClient(clientToSave));
  }

  @Test
  public void testInvalidSliceNum() {
    // Make negative slice num
  }

  @Test
  public void deleteNonExistingClient() {

  }
}
