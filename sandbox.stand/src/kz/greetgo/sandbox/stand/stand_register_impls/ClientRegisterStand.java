package kz.greetgo.sandbox.stand.stand_register_impls;

import com.itextpdf.text.DocumentException;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.sandbox.stand.client_records_report.ClientRecordRow;
import kz.greetgo.sandbox.stand.client_records_report.ClientRecordsReportView;
import kz.greetgo.sandbox.stand.client_records_report.ClientRecordsViewPdf;
import kz.greetgo.sandbox.stand.client_records_report.ClientRecordsViewXlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.*;

@Bean
public class ClientRegisterStand implements ClientRegister {

  @SuppressWarnings("WeakerAccess")
  public BeanGetter<StandDb> db;

  private List<ClientDot> filter(ClientRecordFilter filter) {
    if (filter.searchName == null) {
      return getClientSlice(db.get().clientDots, 0, filter.sliceNum);
    }
    filter.searchName = filter.searchName.toLowerCase().trim();

    return db.get().clientDots
      .stream()
      .filter(clientDot -> filter.searchName == null || fioForSearch(clientDot).contains(filter.searchName))
      .collect(Collectors.toList());
  }

  private String fioForSearch(ClientDot dot) {
    return (dot.surname + " " + dot.name + " " + (dot.patronymic == null ? "" : dot.patronymic)).toLowerCase();
  }

  private ClientRecord dotToClientRecord(ClientDot clientDot) {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = clientDot.id;
    clientRecord.name = clientDot.name;
    clientRecord.surname = clientDot.surname;
    clientRecord.patronymic = clientDot.patronymic;
    clientRecord.age = getDiffYears(clientDot.birthDate, new Date());
    clientRecord.charm = getCharacterById(clientDot.charm);

    clientRecord.minBalance = minMoneyInClientAccounts(clientDot.id);
    clientRecord.maxBalance = maxMoneyInClientAccounts(clientDot.id);
    clientRecord.accBalance = totalMoneyInClientAccounts(clientDot.id);
    return clientRecord;
  }

  private double totalMoneyInClientAccounts(int clientId) {
    double total = 0;
    for (ClientAccountDot clientAccountDot : db.get().clientAccountDots) {
      if (clientAccountDot.id == clientId) {
        total += clientAccountDot.money;
      }
    }
    return total;
  }

  private double maxMoneyInClientAccounts(int clientId) {
    double max = 0;
    for (ClientAccountDot clientAccount : db.get().clientAccountDots) {
      if (clientAccount.id == clientId) {
        if (clientAccount.money > max) {
          max = clientAccount.money;
        }
      }
    }
    return max;
  }

  private double minMoneyInClientAccounts(int clientId) {
    double min = Double.MAX_VALUE;
    for (ClientAccountDot clientAccount : db.get().clientAccountDots) {
      if (clientAccount.id == clientId) {
        if (clientAccount.money < min) {
          min = clientAccount.money;
        }
      }
    }
    return min;
  }

  @Override
  public void deleteClient(int clientId) {
    if (clientId == -1) {
      return;
    }
    db.get().clientDots.removeIf(client -> clientId == client.id);
  }

  @Override
  public ClientDetails details(int clientId) {
    ClientDetails foundClient = new ClientDetails();

    ClientDot clientDot = getClientDot(clientId);
    assert clientDot != null;
    foundClient.id = clientDot.id;
    foundClient.name = clientDot.name;
    foundClient.surname = clientDot.surname;
    foundClient.charm = clientDot.charm;
    foundClient.gender = clientDot.gender;
    foundClient.patronymic = clientDot.patronymic;
    foundClient.birthDate = clientDot.birthDate;


    List<PhoneDot> phoneDotList = getPhoneDotsWithId(clientId);
    foundClient.phones = new ArrayList<>();

    for (PhoneDot phoneDot : phoneDotList) {
      Phone phone = new Phone();
      phone.number = phoneDot.number;
      phone.client_id = phoneDot.client_id;
      phone.type = phoneDot.type;
      foundClient.phones.add(phone);
    }

    List<AddressDot> addressDotList = getAddressesWithClientId(foundClient.id);
    foundClient.addresses = new ArrayList<>();

    for (AddressDot addressDot : addressDotList) {
      Address address = new Address();
      address.id = addressDot.client_id;
      address.clientId = addressDot.client_id;
      address.house = addressDot.house;
      address.flat = addressDot.flat;
      address.street = addressDot.street;
      address.type = addressDot.type;

      foundClient.addresses.add(address);
    }

    return foundClient;
  }

  private List<PhoneDot> getPhoneDotsWithId(int clientId) {
    List<PhoneDot> phones = new ArrayList<>();
    for (PhoneDot phoneDot : db.get().phoneDots) {
      if (phoneDot.client_id == clientId) {
        phones.add(phoneDot);
      }
    }
    return phones;
  }

  private List<AddressDot> getAddressesWithClientId(int id) {
    List<AddressDot> addressDots = new ArrayList<>();
    for (AddressDot addressDot : db.get().addressDots) {
      if (addressDot.client_id == id) {
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
      assert clientDot != null;
      if (!editedClient.name.equals(clientDot.name)) {
        clientDot.name = editedClient.name;
      }
    }
    assert clientDot != null;
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
      clientDot.charm = editedClient.charm;
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
        addressDot.client_id = db.get().addressDots.size();
        addressDot.client_id = clientDot.id;
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
          if (address.id == addressDot.client_id) {
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
        phoneDot.client_id = clientDot.id;
        phoneDot.number = phone.number;
        db.get().phoneDots.add(phoneDot);
      }
    }

    if (editedClient.editedPhones != null) {
      for (Phone phone : editedClient.editedPhones) {
        for (PhoneDot phoneDot : db.get().phoneDots) {
          if (phoneDot.client_id == phone.client_id && phoneDot.number.equals(phone.number)) {
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
      if (addressDot.client_id == clientId) {
        addresses.add(addressDot);
      }
    }
    return addresses;
  }

  private AddressDot getAddressWithId(int id) {
    for (AddressDot addressDot : db.get().addressDots) {
      if (id == addressDot.client_id) {
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
  public List<CharmRecord> getCharms() {
    List<CharmRecord> charmRecords = new ArrayList<>();
    for (CharacterDot characterDot : db.get().characterDots) {
      CharmRecord charmRecord = new CharmRecord();
      charmRecord.id = characterDot.id;
      charmRecord.name = characterDot.name;
      charmRecords.add(charmRecord);
    }
    return charmRecords;
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
        clientRecords.sort(Comparator.comparingDouble(o -> o.age));
        break;
      case "total":
        clientRecords.sort(Comparator.comparingDouble(o -> o.accBalance));
        break;
      case "max":
        clientRecords.sort(Comparator.comparingDouble(o -> o.maxBalance));
        break;
      case "min":
        clientRecords.sort(Comparator.comparingDouble(o -> o.minBalance));
        break;
      case "-surname":
        clientRecords.sort((o1, o2) -> (-o1.surname.compareTo(o2.surname)));
        break;
      case "-age":
        clientRecords.sort((o1, o2) -> Integer.compare(o2.age, o1.age));
        break;
      case "-total":
        clientRecords.sort((o1, o2) -> Double.compare(o2.accBalance, o1.accBalance));
        break;
      case "-max":
        clientRecords.sort((o1, o2) -> Double.compare(o2.maxBalance, o1.maxBalance));
        break;
      case "-min":
        clientRecords.sort((o1, o2) -> Double.compare(o2.minBalance, o1.minBalance));
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


  public void renderClientList(ClientRecordFilter filter, String userName, String type, OutputStream outputStream) throws DocumentException, IOException {
    System.out.println("STARTED");

    ClientRecordsReportView output;
    System.out.println("Found type is: " + type);
    switch (type) {
      case "xlsx":
        output = new ClientRecordsViewXlsx(outputStream);
        break;
      default:
        output = new ClientRecordsViewPdf(outputStream);
    }
    filter.sliceNum = db.get().clientDots.size();
    output.start();
    List<ClientRecord> records = getClients(filter);

    for (ClientRecord record : records) {
      ClientRecordRow row = new ClientRecordRow();
      row.id = record.id;
      row.surname = record.surname;
      row.name = record.name;
      row.patronymic = record.patronymic;
      row.accBalance = record.accBalance;
      row.charm = record.charm;
      row.age = record.age;
      System.out.println(record.maxBalance + " " + record.minBalance);
      row.maxBalance = record.maxBalance;
      row.minBalance = record.minBalance;
      output.appendRow(row);
    }
    System.out.println("finished");
    output.finish(userName, new Date());
  }

}
