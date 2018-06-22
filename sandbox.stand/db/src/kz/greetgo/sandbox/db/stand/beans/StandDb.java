package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.AddressTypeEnum;
import kz.greetgo.sandbox.controller.model.GenderEnum;
import kz.greetgo.sandbox.controller.model.PhoneType;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final List<ClientDot> clientsStorage = new ArrayList<>();
  public final List<CharmDot> charms = new ArrayList<>();
  public final List<ClientPhoneDot> phones = new ArrayList<>();
  public final List<ClientAddressDot> addresses = new ArrayList<>();
  public final List<ClientAccountDot> accounts = new ArrayList<>();

  private Random random = new Random();

  @Override
  public void afterInject() throws Exception {
    appendCharms();

    appendClientDetailsList();

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbInitData.txt"), "UTF-8"))) {

      int lineNo = 0;

      while (true) {
        String line = br.readLine();
        if (line == null) break;
        lineNo++;
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0) continue;
        if (trimmedLine.startsWith("#")) continue;

        String[] splitLine = line.split(";");

        String command = splitLine[0].trim();
        switch (command) {
          case "PERSON":
            appendPerson(splitLine, line, lineNo);
            break;
          case "CLIENT_INFO":
            break;
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }
  @SuppressWarnings("unused")
  private void appendCharms() {
    CharmDot charm = new CharmDot();
    charm.id = 1;
    charm.name = "Гипертимный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 2;
    charm.name = "Дистимный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 3;
    charm.name = "Циклоидный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 4;
    charm.name = "Возбудимый";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 5;
    charm.name = "Застревающий";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 6;
    charm.name = "Педантичный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 7;
    charm.name = "Тревожный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new CharmDot();
    charm.id = 8;
    charm.name = "Демонстративный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
  }
  @SuppressWarnings("unused")
  private void appendAddresses(Integer clientId, AddressTypeEnum typeEnum) {
    ClientAddressDot clientAddress = new ClientAddressDot();
    clientAddress.client = clientId;
    clientAddress.type = typeEnum;
    clientAddress.street = RND.intStr(5);
    clientAddress.house = RND.intStr(5);
    clientAddress.flat = RND.intStr(5);
    addresses.add(clientAddress);
  }
  @SuppressWarnings("unused")
  private void appendPhones(Integer clientId, PhoneType type) {
    ClientPhoneDot clientPhone = new ClientPhoneDot();
    clientPhone.client = clientId;
    clientPhone.type = type;
    clientPhone.number = RND.intStr(11);
    phones.add(clientPhone);
  }
  @SuppressWarnings("unused")
  private void appendClientAccounts(Integer clientId) {
    ClientAccountDot clientAccount = new ClientAccountDot();
    clientAccount.id = clientId;
    clientAccount.clientId = clientId;
    clientAccount.money = (float) RND.plusDouble(200, 2);
    clientAccount.number = RND.intStr(10);
    clientAccount.registeredAt = null;
    accounts.add(clientAccount);
  }

  private void appendClientDetailsList() {
    for (int i = 0; i < 100; i++) {
      ClientDot clientDot = new ClientDot();
      clientDot.id = i;
      clientDot.name = NameGenerator.generateName();
      clientDot.surname = NameGenerator.generateName();
      clientDot.gender = random.nextInt(1) == 0 ? GenderEnum.FEMALE : GenderEnum.MALE;
      clientDot.patronymic = NameGenerator.generateName();
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR,random.nextInt(70) + 1930);
      cal.set(Calendar.MONTH,random.nextInt(12));
      cal.set(Calendar.DAY_OF_MONTH,random.nextInt(28));
      Date birthDate = cal.getTime();
      clientDot.birthDate = birthDate;
      clientDot.charmId = charms.get(random.nextInt(charms.size())).id;
      appendAddresses(clientDot.id, AddressTypeEnum.FACT);
      appendAddresses(clientDot.id, AddressTypeEnum.REG);
      appendPhones(clientDot.id, PhoneType.HOME);
      appendPhones(clientDot.id, PhoneType.MOBILE);
      appendPhones(clientDot.id, PhoneType.WORK);
      for (int g = 0; g < random.nextInt(9) + 1; g++)
        appendClientAccounts(clientDot.id);
      clientsStorage.add(clientDot);
    }
  }

  @SuppressWarnings("unused")
  private void appendPerson(String[] splitLine, String line, int lineNo) {
    PersonDot p = new PersonDot();
    p.id = splitLine[1].trim();
    String[] ap = splitLine[2].trim().split("\\s+");
    String[] fio = splitLine[3].trim().split("\\s+");
    p.accountName = ap[0];
    p.password = ap[1];
    p.surname = fio[0];
    p.name = fio[1];
    if (fio.length > 2) p.patronymic = fio[2];
    personStorage.put(p.id, p);
  }
}

class NameGenerator {

  private static String[] Beginning = {"Kr", "Ca", "Ra", "Mrok", "Cru",
    "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
    "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
    "Mar", "Luk"};
  private static String[] Middle = {"air", "ir", "mi", "sor", "mee", "clo",
    "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
    "marac", "zoir", "slamar", "salmar", "urak"};
  private static String[] End = {"d", "ed", "ark", "arc", "es", "er", "der",
    "tron", "med", "ure", "zur", "cred", "mur"};

  private static Random rand = new Random();

  static String generateName() {

    return Beginning[rand.nextInt(Beginning.length)] +
      Middle[rand.nextInt(Middle.length)] +
      End[rand.nextInt(End.length)];

  }

}