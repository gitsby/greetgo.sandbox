package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
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
    Integer clientId = null;
    assertThat(clientRegister.get().getClientDetails(clientId)).isNull();
  }

  @Test
  public void searchForEmptyName() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = "a";

    assertThat(clientRegister.get().getClients(clientRecordFilter));
    System.out.println(clientRegister.get().getClients(clientRecordFilter).size());
  }

  @Test
  public void testInvalidSliceNum() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = -1;
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }

  @Test
  public void tooBigSliceNum() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 1000000000;
    clientRecordFilter.searchName = "";

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }

  @Test
  public void invalidPaginationPage() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = -1;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = "";

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }

  @Test
  public void getNonExistingPaginationPage() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 900;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = "";

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }


  @Test
  public void testCreateClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.id = -1;

    clientToSave.charm = 1;
    // Send new client

    assertThat(clientRegister.get().save(clientToSave));
  }

  @Test
  public void testCreateClientWithNotExistingCharacter() {

    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.id = null;

    clientToSave.charm = -1;
    // Send new client

    assertThat(clientRegister.get().save(clientToSave));
  }

  @Test
  public void testCreateClientWithNonExistingGender() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = RND.str(10);
    clientToSave.surname = RND.str(10);
    clientToSave.patronymic = RND.str(10);
    clientToSave.gender = "OTHER";
    clientToSave.birthDate = new Date();
    clientToSave.id = null;

    clientToSave.charm = -1;
    // Send new client

    assertThat(clientRegister.get().save(clientToSave));
  }

  @Test
  public void deleteNonExistingClient() {

  }
}
