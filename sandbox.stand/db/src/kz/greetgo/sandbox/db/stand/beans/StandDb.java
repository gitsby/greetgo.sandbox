package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.util.RND;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Bean
public class StandDb implements HasAfterInject {
    Random random;

    public final Map<String, PersonDot> personStorage = new HashMap<>();
    public final List<Client> clientDotList = new ArrayList<>();

    private final int sliceNum = 10;

    @Override
    public void afterInject() throws Exception {
        random = new Random();

        initClientList();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("StandDbInitData.txt"), "UTF-8"))) {

            int lineNo = 0;

            while (true) {
                for (Client client : clientDotList) {
                    System.out.println(client.accBalance);
                }
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

    public int getPaginationNum() {
        return clientDotList.size() / sliceNum;
    }

    private void initClientList() {
        for (int i = 0; i < 100; i++) {
            Client d = new Client();

            d.snmn = RND.str(50);
            d.age = random.nextInt(50) + 18;
            d.character = giveRandomCharacter();
            System.out.println(d.accBalance);
            clientDotList.add(d);
        }
    }

    private String giveRandomCharacter() {
        int randNum = random.nextInt(5);
        switch (randNum) {
            case 0:
                return "Angry";
            case 1:
                return "Scrappy";
            case 2:
                return "Cold-blooded";
            case 3:
                return "Careful";
            case 4:
                return "Relaxed";
            default:
                return "Empty";

        }
    }

    public List<Client> getClientSlice(String paginationPage) {
        List<Client> clients = new ArrayList<>();
        int currentPagination = Integer.parseInt(paginationPage);
        int startSlice = currentPagination * sliceNum;
        int endSlice = sliceNum * currentPagination;
        endSlice += sliceNum;

        if (endSlice > clientDotList.size()) {
            endSlice = clientDotList.size();
        }
        clients = clientDotList.subList(startSlice, endSlice);
        return clients;
    }

    public boolean deleteClient(String clientId) {
        System.out.println("Size:" + clientDotList.size());
        clientDotList.remove(Integer.parseInt(clientId));
        System.out.println("Size:" + clientDotList.size());
        return true;
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
