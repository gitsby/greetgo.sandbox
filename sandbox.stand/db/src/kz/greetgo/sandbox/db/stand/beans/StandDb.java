package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.db.stand.model.*;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
    Random random;

    public final Map<String, PersonDot> personStorage = new HashMap<>();
    public final List<ClientToSave> clientDotList = new ArrayList<>();

    List<ClientDot> clientDots = new ArrayList<>();
    List<PhoneDot> phoneDots = new ArrayList<>();
    List<AddressDot> addressDots = new ArrayList<>();
    List<ClientAccountDot> clientAccountDots = new ArrayList<>();

    public List<PhoneDot> getPhoneDots() {
        return phoneDots;
    }

    public List<AddressDot> getAddressDots() {
        return addressDots;
    }

    public final CharacterDot[] characters = {
            new CharacterDot(),
            new CharacterDot(),
            new CharacterDot(),
            new CharacterDot(),
            new CharacterDot()};

    private final int sliceNum = 10;

    public CharacterDot[] getCharacterDots() {
        return characters;
    }

    public List<ClientAccountDot> getClientAccountDots() {
        return clientAccountDots;
    }

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
            accountDot.clientId = i;
            accountDot.id = i;
            accountDot.money = (float) random.nextInt(200);
            clientAccountDots.add(accountDot);
        }
    }

    private void initClientPhones() {
        for (int i = 0; i < 100; i++) {
            PhoneDot mobile1 = new PhoneDot();
            mobile1.client = i;
            mobile1.number = RND.str(15);
            mobile1.type = "MOBILE";
            phoneDots.add(mobile1);

            PhoneDot home = new PhoneDot();
            home.client = i;
            home.number = RND.str(15);
            home.type = "HOME";

            phoneDots.add(home);
        }
    }

    public List<PhoneDot> getPhoneDotsWithId(int clientId) {
        List<PhoneDot> phones = new ArrayList<>();
        for (PhoneDot phoneDot : phoneDots) {
            if (phoneDot.client == clientId) {
                phones.add(phoneDot);
            }
        }
        return phones;
    }

    public List<AddressDot> getAddressDotsWithId(int clientId) {
        List<AddressDot> addresses = new ArrayList<>();
        for (AddressDot addressDot : addressDots) {
            if (addressDot.clientId == clientId) {
                addresses.add(addressDot);
            }
        }
        return addresses;
    }

    public List<ClientAccountDot> getClientAccountWithId(int clientId) {
        List<ClientAccountDot> accounts = new ArrayList<>();
        for (ClientAccountDot clientAccountDot : clientAccountDots) {
            if (clientAccountDot.clientId == clientId) {
                accounts.add(clientAccountDot);
            }
        }
        return accounts;
    }

    private void initClientAddresses() {
        for (int i = 0; i < 100; i++) {
            AddressDot fact = new AddressDot();
            fact.id = i;
            fact.clientId = i;
            fact.flat = RND.str(10);
            fact.street = RND.str(10);
            fact.house = RND.str(10);
            fact.type = "FACT";

            addressDots.add(fact);

            AddressDot reg = new AddressDot();
            reg.id = i + 1;
            reg.clientId = i;
            reg.flat = RND.str(10);
            reg.street = RND.str(10);
            reg.house = RND.str(10);
            reg.type = "REG";

            addressDots.add(reg);
        }
    }

    private void initClientDots() {
        for (int i = 0; i < 100; i++) {
            ClientDot clientDot = new ClientDot();
            clientDot.id = i;
            clientDot.name = RND.str(10);
            clientDot.surname = RND.str(10);
            clientDot.patronymic = RND.str(10);
            clientDot.gender = RND.str(10);
            clientDot.birthDate = "2012-12-12";
            clientDot.charm = random.nextInt(characters.length);
            clientDots.add(clientDot);
        }
    }

    private void initCharacters() {
        characters[0].name = "Angry";
        characters[0].id = 0;

        characters[1].name = "Scrappy";
        characters[1].id = 1;

        characters[2].name = "Cold-blooded";
        characters[2].id = 2;

        characters[3].name = "Careful";
        characters[3].id = 3;

        characters[4].name = "Relaxed";
        characters[4].id = 4;
    }

    public List<ClientDot> getClientDot() {
        return clientDots;
    }


    private Address[] createRandomAddresses() {
        Address[] addresses = new Address[2];
        addresses[0] = new Address();
        addresses[0].street = RND.str(10);
        addresses[0].house = RND.str(10);
        addresses[0].flat = RND.str(10);
        addresses[0].type = "FACT";

        addresses[1] = new Address();
        addresses[1].street = RND.str(10);
        addresses[1].house = RND.str(10);
        addresses[1].flat = RND.str(10);
        addresses[1].type = "REG";
        return addresses;
    }

    private Phone[] createRandomPhoneNumbers() {
        Phone[] phones = new Phone[3];
        phones[0] = new Phone();
        phones[0].number = RND.str(10);
        phones[0].type = "MOBILE";

        phones[1] = new Phone();
        phones[1].number = RND.str(10);
        phones[1].type = "HOME";

        phones[2] = new Phone();
        phones[2].number = RND.str(10);
        phones[2].type = "WORKING";

        return phones;
    }

    private CharacterDot giveRandomCharacter() {
        int randNum = random.nextInt(characters.length);
        return characters[randNum];
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
