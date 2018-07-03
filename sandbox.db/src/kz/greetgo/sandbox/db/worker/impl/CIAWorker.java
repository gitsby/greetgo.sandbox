package kz.greetgo.sandbox.db.worker.impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
import kz.greetgo.sandbox.db.worker.Worker;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CIAWorker extends Worker {

  private XMLReader xmlReader;

  private PreparedStatement clientPs;
  private PreparedStatement clientAddressPs;
  private PreparedStatement clientPhonePs;

  private String clientTmp;
  private String clientAddressTmp;
  private String clientPhoneTmp;

  private File clientCsvFile;
  private File clientAddressCsvFile;
  private File clientPhoneCsvFile;

  private BufferedWriter clientBw;
  private BufferedWriter clientAddressBw;
  private BufferedWriter clientPhoneBw;

  private final String TMP_DIR = "build/tmp/";

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
  public void prepareStatements() throws SQLException {
    clientPs = nextConncetion().prepareStatement("");
    clientAddressPs = nextConncetion().prepareStatement("");
    clientPhonePs = nextConncetion().prepareStatement("");
  }

  @Override
  public void createTmpTables() throws SQLException {
    createTmpNames();
    exec("CREATE TABLE TMP_TABLE (id VARCHAR(255) NOT NULL, surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birthDate varchar(255), charm VARCHAR(255))", clientTmp);
    exec("CREATE TABLE TMP_TABLE (client_id VARCHAR(255) NOT NULL, type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))", clientAddressTmp);
    exec("CREATE TABLE TMP_TABLE (client_id VARCHAR(255) NOT NULL, type VARCHAR(255), number VARCHAR(255))", clientPhoneTmp);
  }

  private void createTmpNames() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date nowDate = new Date();
    clientTmp = "cia_migration_client_" + sdf.format(nowDate);
    clientAddressTmp = "cia_migration_client_address_" + sdf.format(nowDate);
    clientPhoneTmp = "cia_migration_client_phone_" + sdf.format(nowDate);
  }

  @Override
  public void createCsvFiles() {
    clientCsvFile = createFile(TMP_DIR+"client.csv");
    clientAddressCsvFile = createFile(TMP_DIR+"client_address.csv");
    clientPhoneCsvFile = createFile(TMP_DIR+"client_phone.csv");
    try {
      clientBw = new BufferedWriter(new PrintWriter(clientCsvFile));
      clientAddressBw = new BufferedWriter(new PrintWriter(clientAddressCsvFile));
      clientPhoneBw = new BufferedWriter(new PrintWriter(clientAddressCsvFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private File createFile(String path) {
    File file = new File(path);
    if (!file.exists()) {
      try {
        new File(file.getParent()).mkdirs();
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return file;
  }

  @Override
  public void loadCsvFile() {
    try {
      xmlReader.parse(new InputSource(inputStream));
    } catch (Exception e) {
      e.getMessage();
    }
  }

  private void parse(TMPClient tmpClient, TMPClientAddresses tmpClientAddresses, TMPClientPhones tmpClientPhones) throws Exception {
    writeTmpClient(tmpClient);
    writeTmpClientAddress(tmpClient.id, tmpClientAddresses.addressFact);
    writeTmpClientAddress(tmpClient.id, tmpClientAddresses.addressReg);
    for (ClientPhone phone : tmpClientPhones.phones) {
      writeTmpClientPhone(tmpClient.id, phone);
    }
  }

  private void writeTmpClient(TMPClient client) throws IOException {
    clientBw.write(String.format("%s|%s|%s|%s|%s|%s|%s\n", client.id, checkStr(client.surname), checkStr(client.name), checkStr(client.patronymic), checkStr(client.gender), checkStr(client.birthDate), checkStr(client.charm)));
  }

  private void writeTmpClientAddress(String clientId, ClientAddress address) throws IOException {
    clientAddressBw.write(String.format("%s|%s|%s|%s|%s\n", clientId, checkStr(address.type.name()), checkStr(address.street), checkStr(address.house), checkStr(address.flat)));
  }

  private void writeTmpClientPhone(String clientId, ClientPhone phone) throws IOException {
    clientPhoneBw.write(String.format("%s|%s|%s\n", clientId, checkStr(phone.type.name()), checkStr(phone.number)));
  }

  @Override
  public void loadCsvFilesToTmp(){
    CopyManager copyManager = null;
    try {
      clientBw.flush();
      clientAddressBw.flush();
      clientPhoneBw.flush();

      copyManager = new CopyManager((BaseConnection) nextConncetion());
    } catch (Exception e) {
      System.out.println(e);
    }

    copy(copyManager, clientCsvFile, clientTmp);
    copy(copyManager, clientAddressCsvFile, clientAddressTmp);
    copy(copyManager, clientPhoneCsvFile, clientPhoneTmp);
  }

  private void copy(CopyManager copyManager, File file, String tmp) {
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    try (FileReader reader = new FileReader(file)) {
      copyManager.copyIn(r(copyQuery, tmp), reader);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private String checkStr(String str) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) return str;
    return "\\N";
  }

  @Override
  public void finish() throws SQLException, IOException {
    clientPs.close();
    clientAddressPs.close();
    clientPhonePs.close();

    clientBw.close();
    clientAddressBw.close();
    clientPhoneBw.close();


    //clientCsvFile.delete();
    //clientAddressCsvFile.delete();
    //clientPhoneCsvFile.delete();
    info("FINISH ALL");
  }

  class XMLHandler extends DefaultHandler {

    private String thisValues = "";
    private TMPClient tmpClient = new TMPClient();
    private TMPClientAddresses tmpClientAddresses = new TMPClientAddresses();
    private TMPClientPhones tmpClientPhones = new TMPClientPhones();

    @Override
    public void startDocument() {
      info("Start document");
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
          if (attributes.getValue("value").toLowerCase().equals("male")) tmpClient.gender = GenderEnum.MALE.name();
          else tmpClient.gender = GenderEnum.FEMALE.name();
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
      try {
        parse(tmpClient, tmpClientAddresses, tmpClientPhones);
      } catch (Exception e) {
        System.out.println(e);
      }

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
      }
    }

    @Override
    public void endDocument() {
      info("End document.");
    }
  }
}
