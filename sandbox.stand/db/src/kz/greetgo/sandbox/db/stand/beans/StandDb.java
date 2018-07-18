package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
  Random random;

  public final Map<String, PersonDot> personStorage = new HashMap<>();

  public List<ClientDot> clientDots = new ArrayList<>();
  public List<PhoneDot> phoneDots = new ArrayList<>();
  public List<AddressDot> addressDots = new ArrayList<>();
  public List<ClientAccountDot> clientAccountDots = new ArrayList<>();

  public List<CharacterDot> characterDots = new ArrayList<>();

  String[] randomName = {"Jason", "Steven", "Delphi", "Tomas", "Fred", "Pindre", "Andro"
    , "Ford", "Klinton", "George", "Mikael", "Alden", "Frank", "Sebastian", "Pupy", "Maximus",
    "Sonre", "Nox", "Nyx", "Taurus", "Talmit", "Ferdinand", "Somali", "Freddy", "Bart", "Aquinas",
    "Talos" };

  @Override
  public void afterInject() throws Exception {
    random = new Random();

    initCharacters();
    initClientDots();
    initClientAddresses();
    initClientPhones();
    initClientAccs();

    try (BufferedReader br = new BufferedReader(
      new InputStreamReader(getClass().getResourceAsStream("StandDbInitData.txt"), "UTF-8"))) {

      int lineNo = 0;

      while (true) {
        System.out.println("Check logs");
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
          default:
            throw new RuntimeException("Unknown command " + command);
        }
      }
    }
  }

  private void initClientAccs() {
    for (int i = 0; i < 100; i++) {
      ClientAccountDot accountDot = new ClientAccountDot();
      accountDot.client_id = i;
      accountDot.id = i;
      accountDot.money = (float) random.nextInt(200);
      clientAccountDots.add(accountDot);
    }
  }

  private void initClientPhones() {
    for (int i = 0; i < 100; i++) {
      PhoneDot mobile1 = new PhoneDot();
      mobile1.client_id = i;
      mobile1.number = 7 + "" + random.nextInt(1000) + 1000;
      mobile1.type = "MOBILE";
      phoneDots.add(mobile1);

      PhoneDot home = new PhoneDot();
      home.client_id = i;
      home.number = 7 + "" + random.nextInt(1000) + 1000;
      home.type = "HOME";

      phoneDots.add(home);
    }
  }

  private void initClientAddresses() {
    int id = 0;
    for (int i = 0; i < 100; i++) {
      AddressDot fact = new AddressDot();
      fact.client_id = i;
      fact.flat = RND.str(10);
      fact.street = RND.str(10);
      fact.house = RND.str(10);
      fact.type = "FACT";
      id++;

      addressDots.add(fact);

      AddressDot reg = new AddressDot();
      reg.client_id = i;
      reg.flat = RND.str(10);
      reg.street = RND.str(10);
      reg.house = RND.str(10);
      reg.type = "REG";

      addressDots.add(reg);
      id++;
    }
  }

  private void initClientDots() {
    for (int i = 0; i < 100; i++) {
      ClientDot clientDot = new ClientDot();
      clientDot.id = i;
      clientDot.name = randomName[random.nextInt(randomName.length)];
      clientDot.surname = randomName[random.nextInt(randomName.length)];
      clientDot.patronymic = randomName[random.nextInt(randomName.length)];

      clientDot.gender = (random.nextInt(1) == 0) ? "MALE" : "FEMALE";
      clientDot.birthDate = new Date();
      clientDot.charm = random.nextInt(characterDots.size());
      clientDots.add(clientDot);
    }
  }

  private void initCharacters() {
    CharacterDot angry = new CharacterDot();
    angry.id = 0;
    angry.name = "Angry";

    characterDots.add(angry);

    CharacterDot scrappy = new CharacterDot();
    scrappy.id = 1;
    scrappy.name = "Scrappy";

    characterDots.add(scrappy);

    CharacterDot cold = new CharacterDot();
    cold.name = "Cold-blooded";
    cold.id = 2;
    characterDots.add(cold);

    CharacterDot careful = new CharacterDot();
    careful.name = "Careful";
    careful.id = 3;

    characterDots.add(careful);

    CharacterDot relaxed = new CharacterDot();
    relaxed.name = "Relaxed";
    relaxed.id = 4;
    characterDots.add(relaxed);
  }

  public List<ClientDot> getClientDot() {
    return clientDots;
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
