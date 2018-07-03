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

  private Writer clientBw;
  private Writer clientAddressBw;
  private Writer clientPhoneBw;

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
    clientPs = nextConnection().prepareStatement("");
    clientAddressPs = nextConnection().prepareStatement("");
    clientPhonePs = nextConnection().prepareStatement("");
  }

  @Override
  public void createTmpTables() throws SQLException {
    createTmpNames();
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (id VARCHAR(255) NOT NULL, error varchar(255), surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birth_date varchar(255), charm VARCHAR(255))", clientTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client_id VARCHAR(255) NOT NULL, error varchar(255), type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))", clientAddressTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client_id VARCHAR(255) NOT NULL, error varchar(255), type VARCHAR(255), number VARCHAR(255))", clientPhoneTmp);
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
      clientBw = getWriter(clientCsvFile);
      clientAddressBw = new BufferedWriter(new PrintWriter(clientAddressCsvFile));
      clientPhoneBw = new BufferedWriter(new PrintWriter(clientPhoneCsvFile));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e);
    }
  }

  private Writer getWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    BufferedWriter bw = new BufferedWriter(osw, 100_000);
    return new PrintWriter(bw, true);
  }

  private File createFile(String path) {
    File file = new File(path);
    if (!file.exists()) { new File(file.getParent()).mkdirs(); }
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
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
    clientBw.write(String.format("%s|\\N|%s|%s|%s|%s|%s|%s\n", client.id, checkStr(client.surname, System.nanoTime()),
      checkStr(client.name, System.nanoTime()), checkStr(client.patronymic, System.nanoTime()),
      checkStr(client.gender, System.nanoTime()), checkStr(client.birthDate, System.nanoTime()),
      checkStr(client.charm, System.nanoTime())));
  }

  private void writeTmpClientAddress(String clientId, ClientAddress address) throws IOException {
    clientAddressBw.write(String.format("%s|\\N|%s|%s|%s|%s\n", clientId, checkStr(address.type.name(), null),
      checkStr(address.street, System.nanoTime()), checkStr(address.house,System.nanoTime()),
      checkStr(address.flat,System.nanoTime())));
  }

  private void writeTmpClientPhone(String clientId, ClientPhone phone) throws IOException {
    clientPhoneBw.write(String.format("%s|\\N|%s|%s\n", clientId, checkStr(phone.type.name(),null), checkStr(phone.number, null)));
  }

  private String checkStr(String str, Long counter) {
    if (str == null) return "\\N";
    str = str.trim();
    if (!str.isEmpty()) {
      if (counter == null) return str;
      else return counter + "#" + str;
    }
    return "\\N";
  }

  @Override
  public void loadCsvFilesToTmp(){
    CopyManager copyManager = null;
    try {
      clientBw.flush();
      clientAddressBw.flush();
      clientPhoneBw.flush();
      copyManager = new CopyManager((BaseConnection) nextConnection());
    } catch (Exception e) {
      System.out.println(e);
    }

    copy(copyManager, clientCsvFile, clientTmp);
    copy(copyManager, clientAddressCsvFile, clientAddressTmp);
    copy(copyManager, clientPhoneCsvFile, clientPhoneTmp);
  }

  private void copy(CopyManager copyManager, File file, String tmp) {
    //language=PostgreSQL
    String copyQuery = "COPY TMP_TABLE FROM STDIN WITH DELIMITER '|'";
    try (FileReader reader = new FileReader(file)) {
      copyManager.copyIn(r(copyQuery, tmp), reader);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public void fuseTmpTables() {
    info("FUSE START");
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT id, "
      +"error,"
      +"split_part(max(surname), '#', 2)    AS surname,"
      +"split_part(max(\"name\"), '#', 2)       AS \"name\","
      +"split_part(max(patronymic), '#', 2) AS patronymic,"
      +"split_part(max(gender), '#', 2)     AS gender,"
      +"split_part(max(birth_date), '#', 2) AS birth_date,"
      +"split_part(max(charm), '#', 2)      AS charm "
      +"INTO TMP_TABLE "
      +"FROM TMP_TABLE_conductor "
      +"GROUP BY id, error;", clientTmp);
    info("FUSE END");
  }

  @Override
  public void validateTmpTables() {
    info("VALIDATION START");
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='surname is not defined'" +
      "WHERE error IS NULL AND surname IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='name is not defined'" +
      "WHERE error IS NULL AND \"name\" IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET birth_date=str(birth_date, 'yyyy-MM-dd')", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='birth_date is not defined'" +
      "WHERE error IS NULL AND birth_date IS NULL;", clientTmp);
    info("VALIDATION END");
  }

  @Override
  public void migrateToTables() {

  }

  @Override
  public void deleteTmpTables() {

  }

  @Override
  public void finish() throws SQLException, IOException {
    clientPs.close();
    clientAddressPs.close();
    clientPhonePs.close();

    clientBw.close();
    clientAddressBw.close();
    clientPhoneBw.close();


    clientCsvFile.delete();
    clientAddressCsvFile.delete();
    clientPhoneCsvFile.delete();
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
