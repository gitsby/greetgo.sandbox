package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;

  private List<ClientDot> filter(ClientRecordFilter filter) {
    if (filter.searchName == null) {
      return getClientSlice(db.get().clientDots, 0, filter.sliceNum);
    }
    //fixme searchname trim
    filter.searchName = filter.searchName.toLowerCase();
    List<ClientDot> clientDots = db.get().getClientDot().stream()
      .filter(clientDot -> filter.searchName == null ||
        (clientDot.surname + " " + clientDot.name + " " + clientDot.patronymic).
          toLowerCase().contains(filter.searchName))
      .collect(Collectors.toList());

    return clientDots;
  }

  private ClientRecord dotToClientRecord(ClientDot clientDot) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = clientDot.id;
    clientRecord.name = clientDot.name;
    clientRecord.surname = clientDot.surname;
    clientRecord.patronymic = clientDot.patronymic;
    clientRecord.age = getDiffYears(clientDot.birthDate, new Date());
    clientRecord.character = getCharacterById(clientDot.charm);
    clientRecord.minBalance = (int) db.get().clientAccountDots.get(clientDot.id).money;
    clientRecord.maxBalance = (int) db.get().clientAccountDots.get(clientDot.id).money;
    clientRecord.accBalance = (int) db.get().clientAccountDots.get(clientDot.id).money;
    return clientRecord;
  }

  @Override
  public void deleteClient(int clientId) {
    if (clientId == -1) {
      return;
    }
    db.get().clientDots.removeIf(client -> clientId == client.id);
  }

  @Override
  public ClientDetails getClientDetails(int clientId) {
    ClientDetails foundClient = new ClientDetails();

    ClientDot clientDot = getClientDot(clientId);
    foundClient.id = clientDot.id;
    foundClient.name = clientDot.name;
    foundClient.surname = clientDot.surname;
    foundClient.charm = clientDot.charm;
    foundClient.gender = clientDot.gender;
    foundClient.patronymic = clientDot.patronymic;
    foundClient.birthDate = clientDot.birthDate;


    List<PhoneDot> phoneDotList = getPhoneDotsWithId(clientId);
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

  public List<PhoneDot> getPhoneDotsWithId(int clientId) {
    List<PhoneDot> phones = new ArrayList<>();
    for (PhoneDot phoneDot : db.get().phoneDots) {
      if (phoneDot.client == clientId) {
        phones.add(phoneDot);
      }
    }
    return phones;
  }

  private List<AddressDot> getAddressesWithClientId(int id) {
    List<AddressDot> addressDots = new ArrayList<>();
    for (AddressDot addressDot : db.get().addressDots) {
      if (addressDot.clientId == id) {
        addressDots.add(addressDot);
      }
    }
    return addressDots;
  }

  @Override
  public ClientRecord save(ClientToSave editedClient) {
    ClientDot clientDot;

    if (editedClient.id == null) {
      clientDot = new ClientDot();
      clientDot.id = db.get().clientDots.get(db.get().clientDots.size() - 1).id + 1;

      db.get().clientDots.add(clientDot);

      ClientAccountDot clientAccountDot = new ClientAccountDot();
      clientAccountDot.id = db.get().clientAccountDots.get(db.get().clientAccountDots.size() - 1).id + 1;
      clientAccountDot.money = 0;
      db.get().clientAccountDots.add(clientAccountDot);
    } else {
      clientDot = getClientDot(editedClient.id);
    }

    if (editedClient.name != null) {
      if (!editedClient.name.equals(clientDot.name)) {
        clientDot.name = editedClient.name;
      }
    }
    System.out.println("Gender:" + editedClient.gender + " " + clientDot.gender);
    if (editedClient.surname != null) {
      if (!editedClient.surname.equals(clientDot.surname)) {
        clientDot.surname = editedClient.surname;
      }
    }

    if (!editedClient.patronymic.equals
      (clientDot.patronymic)) {
      clientDot.patronymic = editedClient.patronymic;
    }

    if (editedClient.birthDate != null) {

      if (!editedClient.birthDate.equals
        (clientDot.birthDate)) {
        clientDot.birthDate = editedClient.birthDate;
      }
    }
    if (editedClient.charm != null) {
      if (!editedClient.charm.equals(clientDot.charm)) {
        clientDot.charm = editedClient.charm;
      }
    }
    if (editedClient.gender != null) {
      if (!editedClient.gender.equals
        (clientDot.gender)) {
        clientDot.gender = editedClient.gender;
      }
    }

    if (editedClient.addedAddresses != null) {
      for (Address address : editedClient.addedAddresses) {
        AddressDot addressDot = new AddressDot();
        addressDot.id = db.get().addressDots.size();
        addressDot.clientId = clientDot.id;
        addressDot.type = address.type;
        addressDot.flat = address.flat;
        addressDot.street = address.street;
        addressDot.house = address.house;
        db.get().addressDots.add(addressDot);
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
        for (AddressDot addressDot : getAddressDotsWithId(address.clientId)) {
          if (address.id == addressDot.id) {
            db.get().addressDots.remove(addressDot);
            break;
          }
        }
      }
    }

    if (editedClient.addedPhones != null) {
      for (Phone phone : editedClient.addedPhones) {
        PhoneDot phoneDot = new PhoneDot();
        phoneDot.type = phone.type;
        phoneDot.client = clientDot.id;
        phoneDot.number = phone.number;
        db.get().phoneDots.add(phoneDot);
      }
    }

    if (editedClient.editedPhones != null) {
      for (Phone phone : editedClient.editedPhones) {
        for (PhoneDot phoneDot : db.get().phoneDots) {
          if (phoneDot.client == phone.client && phoneDot.number.equals(phone.number)) {
            phoneDot.number = phone.editedTo;
            break;
          }
        }
      }
    }
    if (editedClient.deletedPhones != null) {
      for (Phone phone : editedClient.deletedPhones) {
        db.get().phoneDots.removeIf(phoneDot -> phone.number.equals(phoneDot.number));
      }
    }

    return dotToClientRecord(clientDot);
  }

  private List<AddressDot> getAddressDotsWithId(int clientId) {
    List<AddressDot> addresses = new ArrayList<>();
    for (AddressDot addressDot : db.get().addressDots) {
      if (addressDot.clientId == clientId) {
        addresses.add(addressDot);
      }
    }
    return addresses;
  }

  private AddressDot getAddressWithId(int id) {
    for (AddressDot addressDot : db.get().addressDots) {
      if (id == addressDot.id) {
        return addressDot;
      }
    }
    return null;
  }

  private ClientDot getClientDot(Integer id) {
    for (ClientDot clientDot : db.get().clientDots) {
      if (id.equals(clientDot.id)) {
        return clientDot;
      }
    }
    return null;
  }

  @Override
  public List<Character> getCharacters() {
    List<Character> characters = new ArrayList<>();
    for (CharacterDot characterDot : db.get().characterDots) {
      Character character = new Character();
      character.id = characterDot.id;
      character.name = characterDot.name;
      characters.add(character);
    }
    return characters;
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
    for (CharacterDot characterDot : db.get().characterDots) {
      if (characterDot.id == id) {
        return characterDot.name;
      }
    }
    return null;
  }

  private <T> List<T> getClientSlice(List<T> clientRecords, int paginationPage, int sliceNum) {
    List<T> clients;
    int startSlice = paginationPage * sliceNum;
    int endSlice = sliceNum * paginationPage + sliceNum;

    if (endSlice > clientRecords.size()) {
      endSlice = clientRecords.size();
    }

    clients = clientRecords.subList(startSlice, endSlice);

    return clients;
  }

  @Override
  public List<ClientRecord> getClients(ClientRecordFilter clientRecordFilter) {
    List<ClientDot> clientDotList = filter(clientRecordFilter);
    clientDotList.sort((o1, o2) -> (Integer.compare(o2.id, o1.id)));
    List<ClientRecord> clientRecords = new ArrayList<>();
    for (ClientDot clientDot : clientDotList) {
      clientRecords.add(dotToClientRecord(clientDot));
    }
    switch (clientRecordFilter.columnName) {
      case "surname":
        clientRecords.sort(Comparator.comparing(o -> o.surname));
        break;
      case "age":
        clientRecords.sort(Comparator.comparingInt(o -> o.age));
        break;
      case "total":
        clientRecords.sort(Comparator.comparingInt(o -> o.accBalance));
        break;
      case "max":
        clientRecords.sort(Comparator.comparingInt(o -> o.maxBalance));
        break;
      case "min":
        clientRecords.sort(Comparator.comparingInt(o -> o.minBalance));
        break;
      case "-surname":
        clientRecords.sort((o1, o2) -> (-o1.surname.compareTo(o2.surname)));
        break;
      case "-age":
        clientRecords.sort((o1, o2) -> Integer.compare(o2.age, o1.age));
        break;
      case "-total":
        clientRecords.sort((o1, o2) -> Integer.compare(o2.accBalance, o1.accBalance));
        break;
      case "-max":
        clientRecords.sort((o1, o2) -> Integer.compare(o2.maxBalance, o1.maxBalance));
        break;
      case "-min":
        clientRecords.sort((o1, o2) -> Integer.compare(o2.minBalance, o1.minBalance));
        break;
    }
    clientRecords = getClientSlice(clientRecords, clientRecordFilter.paginationPage, clientRecordFilter.sliceNum);

    return clientRecords;
  }

  @Override
  public int getClientCount(ClientRecordFilter clientRecordFilter) {
    List<ClientDot> records = filter(clientRecordFilter);
    return records.size();
  }

}
