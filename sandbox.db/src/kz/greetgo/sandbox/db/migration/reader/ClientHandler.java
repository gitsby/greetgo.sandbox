package kz.greetgo.sandbox.db.migration.reader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends DefaultHandler {

  private ClientFromMigration client;

  private ClientProcessor processor;

  private PhoneProcessor phoneProcessor;

  private boolean isMobilePhone = false;
  private boolean isWorkPhone = false;
  private boolean isHomePhone = false;

  private int clientBatchSize = 1000;

  private volatile int threadNum = 0;

  private List<ClientFromMigration> clients = new LinkedList<>();

  List<PhoneFromMigration> phones = new LinkedList<>();

  List<ClientSenderThread> clientSenderThreads = new LinkedList<>();
  List<PhoneSenderThread> phoneSenderThreads = new LinkedList<>();

  public ClientHandler(ClientProcessor processor, PhoneProcessor phoneProcessor) {
    this.processor = processor;
    this.phoneProcessor = phoneProcessor;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.charAt(0) == 'c' && qName.charAt(qName.length() - 1) == 't') {
      client = new ClientFromMigration();
      client.id = attributes.getValue(0);
      client.mobilePhone = new ArrayList<>();
      client.workPhone = new ArrayList<>();
      client.homePhone = new ArrayList<>();
      client.addresses = new ArrayList<>();
    }
    if (client == null) {
      return;
    }
    if (qName.charAt(0) == 'n') {
      client.name = attributes.getValue(0);
    }
    if (qName.charAt(0) == 's') {
      client.surname = attributes.getValue(0);
    }
    if (qName.charAt(0) == 'p') {
      client.patronymic = attributes.getValue(0);
    }
    if (qName.charAt(0) == 'b') {
      client.birth = attributes.getValue(0);
    }
    if (qName.charAt(0) == 'g') {
      client.gender = attributes.getValue(0);
    }
    if (qName.charAt(0) == 'c' && qName.charAt(qName.length() - 1) == 'm') {
      client.charm = attributes.getValue(0);
    }
    if (qName.charAt(0) == 'm') {
      isMobilePhone = true;
    }
    if (qName.charAt(0) == 'w') {
      isWorkPhone = true;
    }
    if (qName.charAt(0) == 'h') {
      isHomePhone = true;
    }

    if ((qName.charAt(0) == 'f' || qName.charAt(0) == 'r')) {
      AddressFromMigration address = new AddressFromMigration();
      address.street = attributes.getValue("street");
      address.house = attributes.getValue("house");
      address.flat = attributes.getValue("flat");
      address.type = (qName.charAt(0) == 'f') ? "FACT" : "REG";
      client.addresses.add(address);
    }
  }

  @Override
  public void characters(char ch[], int start, int length) throws SAXException {

    if (isMobilePhone) {

      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.id;
      phone.type = "MOBILE";
      phones.add(phone);
    }
    if (isWorkPhone) {
      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.id;
      phone.type = "WORKING";

      phones.add(phone);
    }
    if (isHomePhone) {
      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.id;
      phone.type = "HOME";

      phones.add(phone);

    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (client != null) {
      if (qName.equals("client")) {
        clients.add(client);
        if (clients.size() < clientBatchSize) {
          return;
        }

        threadNum++;
        while (threadNum > 6) {
          try {
            Thread.sleep(100);

            for (ClientSenderThread thread : clientSenderThreads) {
              if (!thread.isAlive()) {
                thread.join();
                clientSenderThreads.remove(thread);
                break;
              }
            }
            for (PhoneSenderThread thread : phoneSenderThreads) {
              if (!thread.isAlive()) {
                thread.join();
                phoneSenderThreads.remove(thread);
                threadNum--;
                break;
              }
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        List<ClientFromMigration> fromMigrations = new LinkedList<>(clients);

        clientSenderThreads.add(new ClientSenderThread(processor, fromMigrations));
        clientSenderThreads.get(clientSenderThreads.size() - 1).start();

        List<PhoneFromMigration> phonesFromMigr = new LinkedList<>(phones);

        phoneSenderThreads.add(new PhoneSenderThread(phoneProcessor, phonesFromMigr));
        phoneSenderThreads.get(phoneSenderThreads.size() - 1).start();

        clients = new LinkedList<>();
        phones = new LinkedList<>();
      }
    }
  }
}
