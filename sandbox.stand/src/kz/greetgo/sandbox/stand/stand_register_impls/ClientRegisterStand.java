package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.util.*;

import static java.util.Calendar.*;

@Bean
public class ClientRegisterStand implements ClientRegister {


  private BeanGetter<StandDb> db;

  @Override
  public boolean deleteClient(String clientId) {
    if (clientId == null) {
      System.out.println("NULL");
      return false;
    }
    // TODO: 6/4/2018
    db.get().getClientDot().removeIf(client -> clientId.equals(client.id + ""));
    return true;
  }

  @Override
  public ClientToSave getClientById(int clientId) {
    for (ClientDot clientDot : db.get().getClientDot()) {
      if (clientDot.id == clientId) {
        ClientToSave foundClient = new ClientToSave();
        foundClient.id = clientDot.id;
        foundClient.name = clientDot.name;
        foundClient.surname = clientDot.surname;
        foundClient.charm = clientDot.charm;
        foundClient.gender = clientDot.gender;
        foundClient.patronymic = clientDot.patronymic;
        foundClient.birthDate = clientDot.birthDate;


        List<PhoneDot> phoneDotList = db.get().getPhoneDotsWithId(clientId);
        List<Phone> phones = new ArrayList<>();
        for (PhoneDot phoneDot : phoneDotList) {
          Phone phone = new Phone();
          phone.number = phoneDot.number;
          phone.client = phoneDot.client;
          phone.type = phoneDot.type;
          phones.add(phone);
        }
        foundClient.phones = phones.toArray(new Phone[phoneDotList.size()]);

        List<AddressDot> addressDotList = getAddressesWithClientId(foundClient.id);
        List<Address> addresses = new ArrayList<>();

        for (AddressDot addressDot : addressDotList) {
          Address address = new Address();
          address.id = addressDot.id;
          address.clientId = addressDot.clientId;
          address.house = addressDot.house;
          address.flat = addressDot.flat;
          address.street = addressDot.street;
          address.type = addressDot.type;

          addresses.add(address);
        }

        foundClient.addresses = addresses.toArray(new Address[addresses.size()]);
        return foundClient;
      }
    }
    return null;
  }

  private List<AddressDot> getAddressesWithClientId(int id) {
    List<AddressDot> addressDots = new ArrayList<>();
    for (AddressDot addressDot : db.get().getAddressDots()) {
      if (addressDot.clientId == id) {
        addressDots.add(addressDot);
      }
    }
    return addressDots;
  }

  @Override
  public int editedClient(EditClient editedClient) {
    System.out.println("HERE IT GOES");
    System.out.println(editedClient.id);
    if (editedClient.id == null) {
      createNewClient(editedClient);
      return editedClient.id;
    }
    if (editedClient.name != null) {
      if (!editedClient.name.equals(Objects.requireNonNull(getClientDot(editedClient.id)).name)) {
        Objects.requireNonNull(getClientDot(editedClient.id)).name = editedClient.name;
      }
    }
    if (editedClient.surname != null) {
      if (!editedClient.surname.equals(Objects.requireNonNull(getClientDot(editedClient.id)).surname)) {
        Objects.requireNonNull(getClientDot(editedClient.id)).surname = editedClient.surname;
      }
    }
    if (editedClient.patronymic != null) {
      if (!editedClient.patronymic.equals
        (Objects.requireNonNull(getClientDot(editedClient.id)).patronymic)) {
        db.get().getClientDot().get(editedClient.id).patronymic = editedClient.patronymic;
      }
    }
    if (editedClient.birthDate != null) {

      if (!editedClient.birthDate.equals
        (Objects.requireNonNull(getClientDot(editedClient.id)).birthDate)) {
        Objects.requireNonNull(getClientDot(editedClient.id)).birthDate = editedClient.birthDate;
      }
    }
    if (editedClient.charm != null) {
      if (!editedClient.charm.equals(Objects.requireNonNull(getClientDot(editedClient.id)).charm)) {
        Objects.requireNonNull(getClientDot(editedClient.id)).charm = editedClient.charm;
      }
    }
    if (editedClient.gender != null) {
      if (!editedClient.gender.equals
        (Objects.requireNonNull(getClientDot(editedClient.id)).gender)) {
        Objects.requireNonNull(getClientDot(editedClient.id)).gender = editedClient.gender;
      }
    }

    if (editedClient.addedAddresses != null) {
      // Added
      for (Address address : editedClient.addedAddresses) {
        AddressDot addressDot = new AddressDot();
        addressDot.id = db.get().getAddressDots().size();
        addressDot.type = address.type;
        addressDot.flat = address.flat;
        addressDot.street = address.street;
        addressDot.house = address.house;
        db.get().getAddressDots().add(addressDot);
      }
    }

    if (editedClient.editedAddresses != null) {
      // Edited
      for (Address address : editedClient.editedAddresses) {
        AddressDot editedAddress = getAddressWithId(address.id);
        assert editedAddress != null;
        editedAddress.house = address.house;
        editedAddress.street = address.street;
        editedAddress.flat = address.flat;

      }
    }

    if (editedClient.deletedAddresses != null) {
      // Delete
      for (Address address : editedClient.deletedAddresses) {
        for (AddressDot addressDot : db.get().getAddressDotsWithId(address.clientId)) {
          if (address.id == addressDot.id) {
            db.get().getAddressDots().remove(addressDot);
            break;
          }
        }
      }
    }

    if (editedClient.addedPhones != null) {
      for (Phone phone : editedClient.addedPhones) {
        PhoneDot phoneDot = new PhoneDot();
        phoneDot.type = phone.type;
        phoneDot.client = phone.client;
        phoneDot.number = phone.number;
        db.get().getPhoneDots().add(phoneDot);
      }
    }
    if (editedClient.editedPhones != null) {
      for (Phone phone : editedClient.editedPhones) {
        for (PhoneDot phoneDot : db.get().getPhoneDots()) {
          if (phoneDot.client == phone.client && phoneDot.number.equals(phone.number)) {
            phoneDot.number = phone.editedTo;
            break;
          }
        }
      }
    }
    if (editedClient.deletedPhones != null) {
      for (Phone phone : editedClient.deletedPhones) {
        for (PhoneDot phoneDot : db.get().getPhoneDots()) {
          if (phone.number.equals(phoneDot.number)) {
            db.get().getPhoneDots().remove(phoneDot);
            break;
          }
        }
      }
    }
    return -1;
  }

  private AddressDot getAddressWithId(int id) {
    for (AddressDot addressDot : db.get().getAddressDots()) {
      if (id == addressDot.id) {
        return addressDot;
      }
    }
    return null;
  }

  private ClientDot getClientDot(Integer id) {
    for (ClientDot clientDot : db.get().getClientDot()) {
      if (id.equals(clientDot.id)) {
        return clientDot;
      }
    }
    return null;
  }

  @Override
  public List<Character> getCharacters() {
    List<Character> characters = new ArrayList<>();
    System.out.println("Gor chars" + db.get().getCharacterDots());
    for (CharacterDot characterDot : db.get().getCharacterDots()) {
      Character character = new Character();
      character.id = characterDot.id;
      character.name = characterDot.name;
      characters.add(character);
    }
    return characters;
  }

  private void createNewClient(EditClient client) {
    for (ClientDot clientDot : db.get().getClientDot()) {
      System.out.println("BEFORE:" + clientDot.id);
    }
    client.id = db.get().getClientDot().get(db.get().getClientDot().size() - 1).id + 1;
    ClientDot clientDot = new ClientDot();
    clientDot.id = client.id;
    clientDot.name = client.name;
    clientDot.surname = client.surname;
    clientDot.patronymic = client.patronymic;
    clientDot.birthDate = client.birthDate;
    clientDot.gender = client.gender;
    clientDot.charm = client.charm;

    db.get().getClientDot().add(clientDot);
    for (ClientDot cl : db.get().getClientDot()) {
      System.out.println("BEFORE:" + cl.id);
    }
    for (Address address : client.addedAddresses) {
      AddressDot addressDot = new AddressDot();
      addressDot.clientId = client.id;
      addressDot.id = db.get().getAddressDots().get(db.get().getAddressDots().size() - 1).id + 1;
      addressDot.house = address.house;
      addressDot.street = address.street;
      addressDot.flat = address.flat;
      addressDot.type = address.type;
      db.get().getAddressDots().add(addressDot);
    }
    for (Phone phone : client.addedPhones) {
      PhoneDot phoneDot = new PhoneDot();
      phoneDot.client = client.id;
      phoneDot.number = phone.number;
      phoneDot.type = phone.type;
      db.get().getPhoneDots().add(phoneDot);
    }
    ClientAccountDot clientAccountDot = new ClientAccountDot();
    clientAccountDot.id = db.get().getClientAccountDots().get(db.get().getClientAccountDots().size() - 1).id + 1;
    clientAccountDot.money = 0;
    db.get().getClientAccountDots().add(clientAccountDot);
  }


  private List<RecordClient> searchClient(String searchName, int sliceNum) {
    List<RecordClient> searchClients = new ArrayList<>();
    if (searchName == null) {
      return getClientSlice(createRecordList(), "0", sliceNum);
    }
    searchName = searchName.toLowerCase();
    for (ClientDot client : db.get().getClientDot()) {
      String snmn = (client.surname + " " + client.name + " " + client.patronymic).toLowerCase();
      if (snmn.contains(searchName)) {
        RecordClient foundClient = new RecordClient();
        foundClient.id = client.id;
        foundClient.name = client.name;
        foundClient.surname = client.surname;
        foundClient.patronymic = client.patronymic;
        foundClient.character = getCharacterById(client.charm);
        foundClient.age = getDiffYears(client.birthDate, new Date());
        foundClient.accBalance = (int) db.get().getClientAccountDots().get(client.id).money;
        foundClient.minBalance = (int) db.get().getClientAccountDots().get(client.id).money;
        foundClient.maxBalance = (int) db.get().getClientAccountDots().get(client.id).money;
        searchClients.add(foundClient);
      }
    }

    return searchClients;
  }

  private static int getDiffYears(Date first, Date last) {
    Calendar a = getCalendar(first);
    Calendar b = getCalendar(last);
    int diff = b.get(YEAR) - a.get(YEAR);
    if (a.get(MONTH) > b.get(MONTH) ||
      (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
      diff--;
    }
    return diff;
  }

  private static Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(date);
    return cal;
  }

  private String getCharacterById(int id) {
    for (CharacterDot characterDot : db.get().getCharacterDots()) {
      if (characterDot.id == id) {
        return characterDot.name;
      }
    }
    return "Undefined";
  }

  private List<RecordClient> getClientSlice(List<RecordClient> recordClients, String paginationPage, int sliceNum) {
    List<RecordClient> clients;
    int currentPagination = Integer.parseInt(paginationPage);
    int startSlice = currentPagination * sliceNum;
    int endSlice = sliceNum * currentPagination;
    endSlice += sliceNum;

    if (endSlice > recordClients.size()) {
      endSlice = recordClients.size();
    }

    clients = recordClients.subList(startSlice, endSlice);


    return clients;
  }

  @Override
  public List<RecordClient> getClients(String columnName, String paginationPage,
                                       String searchText, int sliceNum) {
//        int currentColumn = Integer.parseInt(columnNum);
    List<RecordClient> sortedList = searchClient(searchText, sliceNum);
    sortedList.sort((o1, o2) -> (Integer.compare(o2.id, o1.id)));
//        System.out.println("Sorting with: " + currentColumn);
    switch (columnName) {
      case "surname":
        sortedList.sort(Comparator.comparing(o -> o.surname));
        break;
      case "age":
        sortedList.sort(Comparator.comparingInt(o -> o.age));
        break;
      case "total":
        sortedList.sort(Comparator.comparingInt(o -> o.accBalance));
        break;
      case "max":
        sortedList.sort(Comparator.comparingInt(o -> o.maxBalance));
        break;
      case "min":
        sortedList.sort(Comparator.comparingInt(o -> o.minBalance));
        break;
      case "-surname":
        sortedList.sort((o1, o2) -> (-o1.surname.compareTo(o2.surname)));
        break;
      case "-age":
        sortedList.sort((o1, o2) -> Integer.compare(o2.age, o1.age));
        break;
      case "-total":
        sortedList.sort((o1, o2) -> Integer.compare(o2.accBalance, o1.accBalance));
        break;
      case "-max":
        sortedList.sort((o1, o2) -> Integer.compare(o2.maxBalance, o1.maxBalance));
        break;
      case "-min":
        sortedList.sort((o1, o2) -> Integer.compare(o2.minBalance, o1.minBalance));
        break;
    }
    sortedList = getClientSlice(sortedList, paginationPage, sliceNum);

    return sortedList;
  }

  @Override
  public int getRequestedPaginationNum(String searchText, int sliceNum) {
    List<RecordClient> records = searchClient(searchText, sliceNum);
    return getPaginationNum(records, sliceNum);
  }

  private int getPaginationNum(List<RecordClient> list, int sliceNum) {
    System.out.println("PAGINATION NUM:" + (list.size() / sliceNum
      + ((list.size() % sliceNum == 0) ? 0 : 1)) + " " + sliceNum + " " + list.size());
    return list.size() / sliceNum
      + ((list.size() % sliceNum == 0) ? 0 : 1);
  }

  private List<RecordClient> createRecordList() {
    List<RecordClient> recordClients = new ArrayList<>();
    for (ClientDot clientDot : db.get().getClientDot()) {
      RecordClient recordClient = new RecordClient();
      recordClient.id = clientDot.id;
      recordClient.name = clientDot.name;
      recordClient.surname = clientDot.surname;
      recordClient.patronymic = clientDot.patronymic;
      recordClient.age = 65;
      for (CharacterDot characterDot : db.get().getCharacterDots()) {
        if (characterDot.id == clientDot.charm) {
          recordClient.character = characterDot.name;
        }
      }
      recordClient.minBalance = (int) db.get().getClientAccountDots().get(recordClient.id).money;
      recordClient.maxBalance = (int) db.get().getClientAccountDots().get(recordClient.id).money;
      recordClients.add(recordClient);
    }
    return recordClients;
  }


}
