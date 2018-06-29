package kz.greetgo.sandbox.db.worker.impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.Worker;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CIAWorker extends Worker {

  private XMLReader xmlReader;

  private int clientBatchSize = 0;
  private int clientAddressBatchSize = 0;
  private int clientPhoneBatchSize = 0;

  private PreparedStatement clientPs;
  private PreparedStatement clientAddressPs;
  private PreparedStatement clientPhonePs;

  private String clientTmp;
  private String clientAddressTmp;
  private String clientPhoneTmp;

  public CIAWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    super(connections, inputStream, migrationConfig);
    initReader();
  }

  private void initReader() {
    try {
      xmlReader = XMLReaderFactory.createXMLReader();
    } catch (SAXException e) {
      System.out.println(e);
      System.out.println(49);
    }
    xmlReader.setContentHandler(new XMLHandler());
  }

  @Override
  public void createTables() {
    info("Creating tables.");
    setTmp();
    //language=PostgreSQL
    exec("create table TMP_TABLE(id serial, cia_id varchar(255), error varchar(255), surname varchar(255), name varchar(255), patronymic varchar(255), birth_date varchar(255), gender varchar(255), charm varchar(255), primary key (id));", clientTmp);
    //language=PostgreSQL
    exec("create table TMP_TABLE(id serial, client_id varchar(255), error varchar(255), \"type\" varchar(255), street varchar(255), house varchar(255), flat varchar(255), primary key(id));", clientAddressTmp);
    //language=PostgreSQL
    exec("create table TMP_TABLE(id serial, client_id varchar(255), error varchar(255), \"type\" varchar(255), number varchar(255), primary key(id));", clientPhoneTmp);
  }

  @Override
  public void startLoading() {
    info("Start loading.");
    try {
      xmlReader.parse(getInputSource(inputStream));
    } catch (IOException e) {
      System.out.println(e);
      System.out.println(72);
    } catch (SAXException e) {
      System.out.println(e);
      System.out.println(75);
    }
  }

  private InputSource getInputSource(InputStream inputStream) {
    return new InputSource(new InputStreamReader(inputStream));
  }

  @Override
  public void prepareStatements() throws SQLException {
    info("Prepare statements.");
    //on conflict (id) do update set surname=coalesce(?, TMP_TABLE.surname), "name"=coalesce(?, TMP_TABLE.name), patronymic=coalesce(?, TMP_TABLE.patronymic), birth_date=coalesce(?, TMP_TABLE.birth_date), gender=coalesce(?, TMP_TABLE.gender), charm=coalesce(?, TMP_TABLE.charm);
    clientPs = nextConnection().prepareStatement(r("insert into TMP_TABLE (cia_id, surname, \"name\", patronymic, birth_date, gender, charm) values (?,?,?,?,?,?,?);", clientTmp));
    // on conflict (client_id, type) do update set "type"=coalesce(?,TMP_TABLE.type), street=coalesce(?,TMP_TABLE.street), house=coalesce(?,TMP_TABLE.house), flat=coalesce(?,TMP_TABLE.flat)
    clientAddressPs = nextConnection().prepareStatement(r("insert into TMP_TABLE (client_id, \"type\", street, house, flat) values (?,?,?,?,?);", clientAddressTmp));
    // on conflict (client_id, "type") do update set "type"=coalesce(?, TMP_TABLE.type), number=coalesce(?, TMP_TABLE.number);
    clientPhonePs = nextConnection().prepareStatement(r("insert into TMP_TABLE (client_id, \"type\", number) values (?,?,?);",clientPhoneTmp));
  }

  @Override
  public void checkBatch()  {
    try {
      if (clientBatchSize > migrationConfig.downloadMaxBatchSize()) {
        clientPs.executeBatch();
        clientPs.clearBatch();
        clientBatchSize = 0;
      }
      if (clientAddressBatchSize > migrationConfig.downloadMaxBatchSize()) {
        clientAddressPs.executeBatch();
        clientAddressPs.clearBatch();
        clientAddressBatchSize = 0;
      }
      if (clientPhoneBatchSize > migrationConfig.downloadMaxBatchSize()) {
        clientPhonePs.executeBatch();
        clientPhonePs.clearBatch();
        clientPhoneBatchSize = 0;
      }
    }catch (SQLException e) {
      System.out.println(e.getNextException());
      System.out.println(112);
    }
  }

  @Override
  public void finish() throws SQLException {
    clientPs.executeBatch();
    clientAddressPs.executeBatch();
    clientPhonePs.executeBatch();
    for (Connection connection : connections) connection.commit();
    info("Finish.");
  }


  private void setTmp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String date = sdf.format(new Date());
    clientTmp = "cia_migration_client_" + date;
    clientAddressTmp = "cia_migration_client_address_" + date;
    clientPhoneTmp = "cia_migration_client_phone_" + date;
  }

  private void addBatches(TMPClient tmpClient, TMPClientAddresses tmpClientAddresses, TMPClientPhones tmpClientPhones) {
    try {
      addClientBatch(tmpClient);
      addClientAddressBatch(tmpClient.id, tmpClientAddresses.addressFact);
      addClientAddressBatch(tmpClient.id, tmpClientAddresses.addressReg);
      for (ClientPhone phone : tmpClientPhones.phones)
        addClientPhoneBatch(tmpClient.id, phone);
    } catch (SQLException e) {
      System.out.println(e);
      System.out.println(146);
    }
    checkBatch();
  }

  private void addClientPhoneBatch(String id, ClientPhone phone) throws SQLException {
    clientPhonePs.setObject(1, id);
    clientPhonePs.setObject(2, phone.type.name());
    clientPhonePs.setObject(3, phone.number);
//    clientPhonePs.setObject(4, phone.type.name());
//    clientPhonePs.setObject(5, phone.number);
    clientPhonePs.addBatch();
    clientPhoneBatchSize++;
  }

  private void addClientAddressBatch(String id, ClientAddress address) throws SQLException {
    clientAddressPs.setObject(1, id);
    clientAddressPs.setObject(2, address.type.name());
    clientAddressPs.setObject(3, address.street);
    clientAddressPs.setObject(4, address.house);
    clientAddressPs.setObject(5, address.flat);
//    clientAddressPs.setObject(6, address.type.name());
//    clientAddressPs.setObject(7, address.street);
//    clientAddressPs.setObject(8, address.house);
//    clientAddressPs.setObject(9, address.flat);
    clientAddressPs.addBatch();
    clientAddressBatchSize++;
  }

  private void addClientBatch(TMPClient tmpClient) throws SQLException {
    clientPs.setObject(1, tmpClient.id);
    clientPs.setObject(2, tmpClient.surname);
    clientPs.setObject(3, tmpClient.name);
    clientPs.setObject(4, tmpClient.patronymic);
    clientPs.setObject(5, tmpClient.birthDate);
    clientPs.setObject(6, tmpClient.gender);
    clientPs.setObject(7, tmpClient.charm);
//    clientPs.setObject(8, tmpClient.surname);
//    clientPs.setObject(9, tmpClient.name);
//    clientPs.setObject(10, tmpClient.patronymic);
//    clientPs.setObject(11, tmpClient.birthDate);
//    clientPs.setObject(12, tmpClient.gender);
//    clientPs.setObject(13, tmpClient.charm);
    clientPs.addBatch();
    clientBatchSize++;
  }

  class XMLHandler extends DefaultHandler {

    private String thisValues = "";
    private TMPClient tmpClient = new TMPClient();
    private TMPClientAddresses tmpClientAddresses = new TMPClientAddresses();
    private TMPClientPhones tmpClientPhones = new TMPClientPhones();

    @Override
    public void startDocument() {
      tmpClient = new TMPClient();
      tmpClientAddresses = new TMPClientAddresses();
      tmpClientPhones = new TMPClientPhones();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      thisValues = qName;
      switch (qName) {
        case "client":
          tmpClient.id = attributes.getValue("id");
          break;
        case "surname":
          tmpClient.surname = attributes.getValue("value");
          break;
        case "name":
          tmpClient.name = attributes.getValue("value");
          break;
        case "patronymic":
          tmpClient.patronymic = attributes.getValue("value");
          break;
        case "gender":
          if (attributes.getValue("value").toLowerCase().equals("male")) {
            tmpClient.gender = GenderEnum.MALE.name();
          } else {
            tmpClient.gender = GenderEnum.FEMALE.name();
          }
          break;
        case "charm":
          tmpClient.charm = attributes.getValue("value");
          break;
        case "birth":
          tmpClient.birthDate = attributes.getValue("value");
          break;
        case "fact":
          tmpClientAddresses.addressFact = getAddress(attributes);
          tmpClientAddresses.addressFact.type = AddressTypeEnum.FACT;
          break;
        case "register":
          tmpClientAddresses.addressReg = getAddress(attributes);
          tmpClientAddresses.addressReg.type = AddressTypeEnum.REG;
      }
    }

    private ClientAddress getAddress(Attributes attributes) {
      ClientAddress clientAddress = new ClientAddress();
      clientAddress.street = attributes.getValue("street");
      clientAddress.house = attributes.getValue("house");
      clientAddress.flat = attributes.getValue("flat");
      return clientAddress;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("client")) upload();
      thisValues = "";
    }


    private void upload() {
      addBatches(tmpClient, tmpClientAddresses, tmpClientPhones);

      tmpClient = new TMPClient();
      tmpClientAddresses = new TMPClientAddresses();
      tmpClientPhones = new TMPClientPhones();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      switch (thisValues) {
        case "homePhone":
          tmpClientPhones.phones.add(new ClientPhone(null,PhoneType.HOME,new String(ch, start, length)));
          break;
        case "workPhone":
          tmpClientPhones.phones.add(new ClientPhone(null,PhoneType.WORK,new String(ch, start, length)));
          break;
        case "mobilePhone":
          tmpClientPhones.phones.add(new ClientPhone(null,PhoneType.MOBILE,new String(ch, start, length)));
          break;
      }
    }

    @Override
    public void endDocument() {
      System.out.println("End document.");
    }
  }
}
