package kz.greetgo.sandbox.db.migration.reader.xml;

import kz.greetgo.sandbox.db.migration.reader.AddressSenderThread;
import kz.greetgo.sandbox.db.migration.reader.ClientSenderThread;
import kz.greetgo.sandbox.db.migration.reader.PhoneSenderThread;
import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.ClientFromMigration;
import kz.greetgo.sandbox.db.migration.reader.objects.PhoneFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.AddressProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.ClientProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.PhoneProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends DefaultHandler {

  private ClientFromMigration client;

  private ClientProcessor processor;
  private PhoneProcessor phoneProcessor;
  private AddressProcessor addressProcessor;

  private boolean isMobilePhone = false;
  private boolean isWorkPhone = false;
  private boolean isHomePhone = false;

  public int clientBatchSize = 1000;

  private int threadNum = 0;

  private List<ClientFromMigration> clients = new LinkedList<>();

  private List<PhoneFromMigration> phones = new LinkedList<>();

  private List<AddressFromMigration> addresses = new LinkedList<>();

  private List<ClientSenderThread> clientSenderThreads = new LinkedList<>();
  private List<PhoneSenderThread> phoneSenderThreads = new LinkedList<>();
  private List<AddressSenderThread> addressSenderThreads = new LinkedList<>();

  public ClientHandler(ClientProcessor processor, AddressProcessor addressProcessor, PhoneProcessor phoneProcessor) {
    this.processor = processor;
    this.addressProcessor = addressProcessor;
    this.phoneProcessor = phoneProcessor;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if ("client".equals(qName)) {
      client = new ClientFromMigration();
      client.error = new StringBuilder();
      client.timestamp = new Timestamp(new Date().getTime());
      client.client_id = attributes.getValue(0);
    }
    if (client == null) {
      return;
    }
    if ("name".equals(qName)) {
      client.name = attributes.getValue(0);
    }
    if ("surname".equals(qName)) {
      client.surname = attributes.getValue(0);
    }
    if ("patronymic".equals(qName)) {
      client.patronymic = attributes.getValue(0);
    }
    if ("birth".equals(qName)) {
      client.birth = attributes.getValue(0);
    }
    if ("gender".equals(qName)) {
      client.gender = attributes.getValue(0);
    }
    if ("charm".equals(qName)) {
      client.charm = attributes.getValue(0);
    }
    if ("mobilePhone".equals(qName)) {
      isMobilePhone = true;
    }
    if ("workPhone".equals(qName)) {
      isWorkPhone = true;
    }
    if ("homePhone".equals(qName)) {
      isHomePhone = true;
    }

    if (("fact".equals(qName) || "register".equals(qName))) {
      AddressFromMigration address = new AddressFromMigration();
      address.client_id = client.client_id;
      address.street = attributes.getValue("street");
      address.house = attributes.getValue("house");
      address.flat = attributes.getValue("flat");
      address.type = ("fact".equals(qName)) ? "FACT" : "REG";
      addresses.add(address);
    }
  }

  @Override
  public void characters(char ch[], int start, int length) {

    if (isMobilePhone) {
      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "MOBILE";

      phones.add(phone);
      isMobilePhone = false;
    }
    if (isWorkPhone) {
      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "WORKING";

      phones.add(phone);
      isWorkPhone = false;
    }
    if (isHomePhone) {
      PhoneFromMigration phone = new PhoneFromMigration();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "HOME";

      phones.add(phone);
      isHomePhone = false;

    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    if (client != null) {
      if (qName.equals("client")) {
        clients.add(client);
        if (clients.size() > clientBatchSize) {
          sendClient();
        }

      } else if (qName.equals("cia")) {
        System.out.println();
        sendClient();
      }
    }
  }

  private void sendClient() {
    threadNum++;
    joinDeadThreads();

    List<ClientFromMigration> fromMigrations = new LinkedList<>(clients);

    clientSenderThreads.add(new ClientSenderThread(processor, fromMigrations));
    clientSenderThreads.get(clientSenderThreads.size() - 1).start();

    List<AddressFromMigration> addressFromMigr = new LinkedList<>(addresses);
    addressSenderThreads.add(new AddressSenderThread(addressProcessor, addressFromMigr));
    addressSenderThreads.get(addressSenderThreads.size() - 1).start();

    List<PhoneFromMigration> phonesFromMigr = new LinkedList<>(phones);

    phoneSenderThreads.add(new PhoneSenderThread(phoneProcessor, phonesFromMigr));
    phoneSenderThreads.get(phoneSenderThreads.size() - 1).start();

    clients = new LinkedList<>();
    addresses = new LinkedList<>();
    phones = new LinkedList<>();
  }

  private void joinDeadThreads() {
    while (threadNum > 1) {
      for (ClientSenderThread thread : clientSenderThreads) {
        if (!thread.isAlive()) {
          clientSenderThreads.remove(thread);
          break;
        }
      }

      for (AddressSenderThread thread : addressSenderThreads) {
        if (!thread.isAlive()) {
          addressSenderThreads.remove(thread);
          break;
        }
      }

      for (PhoneSenderThread thread : phoneSenderThreads) {
        if (!thread.isAlive()) {
          phoneSenderThreads.remove(thread);
          threadNum--;
          break;
        }
      }
    }
  }
}
