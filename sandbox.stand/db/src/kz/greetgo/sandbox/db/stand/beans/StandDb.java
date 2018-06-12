package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  public final Map<String, PersonDot> personStorage = new HashMap<>();
  public final List<ClientDot> clientsStorage = new ArrayList<>();
  public final List<Charm> charms = new ArrayList<>();
  public final List<ClientPhone> phones = new ArrayList<>();
  public final List<ClientAddress> addresses = new ArrayList<>();
  public final List<ClientAccount> accounts = new ArrayList<>();

  private Random random = new Random();

  @Override
  public void afterInject() throws Exception {
    appendCharms();

    appendAddresses();
    appendPhones();
    appendClientAccounts();

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
    Charm charm = new Charm();
    charm.id = 1;
    charm.name = "Гипертимный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 2;
    charm.name = "Дистимный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 3;
    charm.name = "Циклоидный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 4;
    charm.name = "Возбудимый";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 5;
    charm.name = "Застревающий";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 6;
    charm.name = "Педантичный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 7;
    charm.name = "Тревожный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
    charm = new Charm();
    charm.id = 8;
    charm.name = "Демонстративный";
    charm.energy = random.nextFloat();
    charm.description = RND.intStr(20);
    charms.add(charm);
  }

  @SuppressWarnings("unused")
  private void appendAddresses() {
    for (int i = 0; i < 100; i++) {
      ClientAddress clientAddress = new ClientAddress();
      clientAddress.id = i;
      clientAddress.type = 1 == random.nextInt(1) ? AddressType.FACT : AddressType.REG;
      clientAddress.street = RND.intStr(5);
      clientAddress.house = RND.intStr(5);
      clientAddress.flat = RND.intStr(5);
      addresses.add(clientAddress);
    }
  }

  @SuppressWarnings("unused")
  private void appendPhones() {
    for(int i = 0; i < 100; i++) {
      ClientPhone clientPhone = new ClientPhone();
      clientPhone.id = i;
      clientPhone.type = 1 == random.nextInt(1) ? PhoneType.HOME : PhoneType.MOBILE;
      clientPhone.number = RND.intStr(10);
      phones.add(clientPhone);
    }
  }

  @SuppressWarnings("unused")
  private void appendClientAccounts() {
    for (int i = 0; i < 100; i++) {
      ClientAccount clientAccount = new ClientAccount();
      clientAccount.id = i;
      clientAccount.money = random.nextFloat()*random.nextInt(10000);
      clientAccount.number = RND.intStr(10);
      clientAccount.registered_at = null;
      accounts.add(clientAccount);
    }
  }

  @SuppressWarnings("unused")
  private void appendClientDetailsList() {
    for (int i = 0; i < 100; i++) {
      ClientDot clientDot = new ClientDot();
      clientDot.id = i;
      clientDot.name = NameGenerator.generateName();
      clientDot.surname = NameGenerator.generateName();
      clientDot.gender = random.nextInt(1) == 0 ? Gender.FEMALE : Gender.MALE;
      clientDot.patronymic = NameGenerator.generateName();
      Date birth_day = new Date();
      birth_day.setYear(random.nextInt(50)+1950);
      birth_day.setMonth(random.nextInt(12));
      birth_day.setDate(random.nextInt(28));
      clientDot.birth_day = birth_day;
      clientDot.charmId = charms.get(random.nextInt(charms.size())).id;
      clientDot.addressRegId = addresses.get(random.nextInt(addresses.size())).id;
      clientDot.addressFactId = addresses.get(random.nextInt(addresses.size())).id;
      clientDot.homePhoneId = phones.get(random.nextInt(phones.size())).id;
      clientDot.workPhoneId = phones.get(random.nextInt(phones.size())).id;
      clientDot.mobilePhoneId = phones.get(random.nextInt(phones.size())).id;
      for (int g = 0; g < random.nextInt(9)+1; g++) clientDot.accountsId.add(accounts.get(random.nextInt(accounts.size())).id);
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

  private static String[] Beginning = { "Kr", "Ca", "Ra", "Mrok", "Cru",
    "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
    "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
    "Mar", "Luk" };
  private static String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
    "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
    "marac", "zoir", "slamar", "salmar", "urak" };
  private static String[] End = { "d", "ed", "ark", "arc", "es", "er", "der",
    "tron", "med", "ure", "zur", "cred", "mur" };

  private static Random rand = new Random();

  static String generateName() {

    return Beginning[rand.nextInt(Beginning.length)] +
      Middle[rand.nextInt(Middle.length)]+
      End[rand.nextInt(End.length)];

  }

}