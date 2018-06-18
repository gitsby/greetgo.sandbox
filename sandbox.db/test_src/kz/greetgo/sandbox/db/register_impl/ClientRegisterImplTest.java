package kz.greetgo.sandbox.db.register_impl;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientRegister> clientRegister;

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<ClientTestDao> testDaoBeanGetter;

  @Test
  public void checkCharactersNotNull() {
    List<CharmRecord> charmRecords = clientRegister.get().charm();
    assertThat(charmRecords).isNotNull();
    for (CharmRecord charmRecord : charmRecords) {
      System.out.println(charmRecord.id + " " + charmRecord.name);
    }
  }

  @Test
  public void deleteClient() {
    clientRegister.get().deleteClient(2);
  }

  @Test
  public void createNewClient() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = "Famir";
    clientToSave.surname = "Fill";
    clientToSave.patronymic = "Tindra";
    clientToSave.gender = "MALE";
    clientToSave.birthDate = new Date();
    clientToSave.charm = 1;
    clientToSave.id = null;

    Phone phone = new Phone();
    phone.number = RND.str(11);
    phone.type = "MOBILE";

    Address address = new Address();
    address.flat = "Flat";
    address.house = "House";
    address.street = "Street";
    address.type = "REG";

    clientToSave.addedPhones = new Phone[1];
    clientToSave.addedPhones[0] = phone;

    clientToSave.addedAddresses = new Address[1];
    clientToSave.addedAddresses[0] = address;
//
//    Address edited = new Address();
//    edited.flat = "Flat1";
//    edited.house = "House1";
//    edited.street = "Street1";
//    edited.type = "REG";
//    clientToSave.editedAddresses = new Address[1];
//    clientToSave.editedAddresses[0] = edited;

    assertThat(clientRegister.get().save(clientToSave)).isNull();
  }

  @Test
  public void testEditClientAddress() {
    ClientToSave clientToSave = new ClientToSave();
    clientToSave.name = "Namy";
    clientToSave.surname = "Andres";
    clientToSave.patronymic = "Torpa";
    clientToSave.gender = "FEMALE";
    clientToSave.birthDate = new Date();
    clientToSave.charm = 2;
    clientToSave.id = 2;

//    Phone phone = new Phone();
//    phone.number = RND.str(11);
//    phone.type = "MOBILE";
//
//    clientToSave.addedPhones = new Phone[1];
//    clientToSave.addedPhones[0] = phone;


    Address edited = new Address();
    edited.flat = "Flat1";
    edited.house = "House1";
    edited.street = "Street1";
    edited.type = "REG";
    clientToSave.editedAddresses = new Address[1];
    clientToSave.editedAddresses[0] = edited;

    assertThat(clientRegister.get().save(clientToSave)).isNull();
  }

  @Test
  public void testGetClientDetails() {
    assertThat(clientRegister.get().details(-100)).isNull();
  }

  @Test
  public void searchForEmptyName() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = "and";

    assertThat(clientRegister.get().getClients(clientRecordFilter));

  }

  @Test
  public void isSortedBySurnameNameAsc() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);


    Comparator<ClientRecord> clientRecordAgeComparator = Comparator.comparing(o -> (o.surname + o.name + o.patronymic));


    assertThat(isSorted(clientRecordAgeComparator, records)).isTrue();

  }

  @Test
  public void isSortedBySurnameNameDesc() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-surname";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);


    Comparator<ClientRecord> clientRecordAgeComparator = (o1, o2) -> (o2.surname + o2.name + o2.patronymic).compareTo(o1.surname + o1.name + o1.patronymic);


    assertThat(isSorted(clientRecordAgeComparator, records)).isTrue();

  }

  @Test
  public void isSortedByAgeAsc() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);

    // Compare
    Comparator<ClientRecord> clientRecordAgeComparator = (o1, o2) -> o1.age >= o2.age ? 1 : -1;


    assertThat(isSorted(clientRecordAgeComparator, records)).isTrue();

  }

  @Test
  public void isSortedByAgeDesc() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "-age";
    clientRecordFilter.paginationPage = 0;
    clientRecordFilter.sliceNum = 10;

    List<ClientRecord> records = clientRegister.get().getClients(clientRecordFilter);


    Comparator<ClientRecord> clientRecordAgeComparator = (o1, o2) -> o1.age <= o2.age ? 1 : -1;


    assertThat(isSorted(clientRecordAgeComparator, records)).isTrue();

  }

  private boolean isSorted(Comparator<ClientRecord> comparator, List<ClientRecord> records) {
    ClientRecord previous = null;

    for (ClientRecord clientRecord : records) {
      if (previous != null && comparator.compare(previous, clientRecord) == -1) {
        return false;
      }
      previous = clientRecord;
    }
    return true;
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
    clientRecordFilter.searchName = null;

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }

  @Test
  public void invalidPaginationPage() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = -1;
    clientRecordFilter.sliceNum = 10;

    assertThat(clientRegister.get().getClients(clientRecordFilter));
  }

  @Test
  public void getNonExistingPaginationPage() {
    ClientRecordFilter clientRecordFilter = new ClientRecordFilter();
    clientRecordFilter.columnName = "empty";
    clientRecordFilter.paginationPage = 900;
    clientRecordFilter.sliceNum = 10;
    clientRecordFilter.searchName = null;

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
