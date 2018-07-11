package kz.greetgo.sandbox.db.worker.impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
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

public class CIAWorker extends Worker {

  private static Logger logger = Logger.getLogger(CIAWorker.class);

  private XMLReader xmlReader;

  private String clientTmp;
  private String clientAddressTmp;
  private String clientPhoneTmp;
  private String charmTmp;

  private File clientCsvFile;
  private File clientAddressCsvFile;
  private File clientPhoneCsvFile;
  private File charmCsvFile;

  private Writer clientCsvBw;
  private Writer clientAddressCsvBw;
  private Writer clientPhoneCsvBw;
  private Writer charmCsvBw;

  public CIAWorker(Connection connection, InputStream inputStream) {
    super(connection, inputStream);
    initReader();
  }

  private void initReader() {
    try {
      xmlReader = XMLReaderFactory.createXMLReader();
    } catch (SAXException e) {
      logger.error(e.getMessage());
    }
    xmlReader.setContentHandler(new XMLHandler());
  }

  @Override
  public void createTmpTables() {
    logger.info("create tmp tables begin...");
    setTmpTableNames();
    exec("CREATE TABLE TMP_TABLE (name VARCHAR(255))", charmTmp);
    exec("CREATE TABLE TMP_TABLE (id VARCHAR(255) NOT NULL, surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birth_date varchar(255), charm VARCHAR(255))", clientTmp);
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255) NOT NULL, type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))", clientAddressTmp);
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255) NOT NULL, type VARCHAR(255), number VARCHAR(255))", clientPhoneTmp);
    logger.info("create tmp tables end.");
  }

  private void setTmpTableNames() {
    charmTmp = getTmpTableName("cia_migration_charm");
    clientTmp = getTmpTableName("cia_migration_client");
    clientAddressTmp = getTmpTableName("cia_migration_client_address");
    clientPhoneTmp = getTmpTableName("cia_migration_client_phone");
  }

  @Override
  public void createCsvFiles() {
    logger.info("create csv files begin...");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    logger.info("create csv files end.");
  }

  private void createFiles() throws IOException {
    charmCsvFile = createFile("charm.csv");
    clientCsvFile = createFile("client.csv");
    clientAddressCsvFile = createFile("client_address.csv");
    clientPhoneCsvFile = createFile("client_phone.csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    charmCsvBw = getWriter(charmCsvFile);
    clientCsvBw = getWriter(clientCsvFile);
    clientAddressCsvBw = getWriter(clientAddressCsvFile);
    clientPhoneCsvBw = getWriter(clientPhoneCsvFile);
  }

  @Override
  public void loadCsvFile() {
    logger.info("load csv files begin...");
    try {
      xmlReader.parse(new InputSource(inputStream));
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    logger.info("load csv files end.");
  }

  private void write(TMPClient tmpClient, TMPClientAddresses tmpClientAddresses, TMPClientPhones tmpClientPhones) throws IOException {
    writeTmpCharmCsv(tmpClient.charm);
    writeTmpClientCsv(tmpClient);
    writeTmpClientAddressCsv(tmpClient.id, tmpClientAddresses.addressFact);
    writeTmpClientAddressCsv(tmpClient.id, tmpClientAddresses.addressReg);
    for (ClientPhone phone : tmpClientPhones.phones) {
      writeTmpClientPhoneCsv(tmpClient.id, phone);
    }
  }

  private void writeTmpCharmCsv(String name) throws IOException {
    charmCsvBw.write(name+"\n");
  }

  private void writeTmpClientCsv(TMPClient client) throws IOException {
    if (!isDate(client.birthDate)) client.birthDate = null;
    clientCsvBw.write(String.format("%s|%s|%s|%s|%s|%s|%s\n", client.id, checkStr(client.surname, System.nanoTime()),
      checkStr(client.name, System.nanoTime()), checkStr(client.patronymic, System.nanoTime()),
      checkStr(client.gender, System.nanoTime()), checkStr(client.birthDate, System.nanoTime()),
      checkStr(client.charm, System.nanoTime())));
  }

  private void writeTmpClientAddressCsv(String clientId, ClientAddress address) throws IOException {
    clientAddressCsvBw.write(String.format("%s|%s|%s|%s|%s\n", clientId, checkStr(address.type.name(), null),
      checkStr(address.street, System.nanoTime()), checkStr(address.house,System.nanoTime()),
      checkStr(address.flat,System.nanoTime())));
  }

  private void writeTmpClientPhoneCsv(String clientId, ClientPhone phone) throws IOException {
    clientPhoneCsvBw.write(String.format("%s|%s|%s\n", clientId, checkStr(phone.type.name(),null), checkStr(phone.number, System.nanoTime())));
  }

  @Override
  public void loadCsvFilesToTmpTables(){
    logger.info("load csv files to tmp tables begin...");
    CopyManager copyManager;
    try {
      flushWriters();

      copyManager = new CopyManager((BaseConnection) connection);

      copy(copyManager, charmCsvFile, charmTmp);
      copy(copyManager, clientCsvFile, clientTmp);
      copy(copyManager, clientAddressCsvFile, clientAddressTmp);
      copy(copyManager, clientPhoneCsvFile, clientPhoneTmp);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    logger.info("load csv files to tmp tables end.");
  }

  @Override
  public void fuseMainTmpTables() {
    logger.info("fuse main tmp tables begin...");
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", charmTmp);
    exec("SELECT DISTINCT name INTO TMP_TABLE FROM TMP_TABLE_conductor GROUP BY name;", charmTmp);

    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientTmp);
    exec("SELECT id, "
      +"CAST(NULL AS VARCHAR(255)) AS error,"
      +"split_part(max(surname), '#', 2)    AS surname,"
      +"split_part(max(\"name\"), '#', 2)   AS \"name\","
      +"split_part(max(patronymic), '#', 2) AS patronymic,"
      +"split_part(max(gender), '#', 2)     AS gender,"
      +"split_part(max(birth_date), '#', 2) AS birth_date,"
      +"split_part(max(charm), '#', 2)      AS charm "
      +"INTO TMP_TABLE "
      +"FROM TMP_TABLE_conductor "
      +"GROUP BY id, error;", clientTmp);

    logger.info("fuse main tmp tables end.");
  }

  @Override
  public void validateMainTmpTables() {
    logger.info("validate main tmp tables begin...");
    exec("UPDATE TMP_TABLE SET error='surname is not defined'" +
      "WHERE error IS NULL AND surname IS NULL;", clientTmp);
    exec("UPDATE TMP_TABLE SET error='name is not defined'" +
      "WHERE error IS NULL AND \"name\" IS NULL;", clientTmp);
    exec("UPDATE TMP_TABLE SET error='birth_date is not defined'" +
      "WHERE error IS NULL AND birth_date IS NULL;", clientTmp);

    logger.info("validate main tmp tables end.");
  }

  @Override
  public void migrateMainTmpTableToTables() {
    logger.info("migrate main tmp tables to tables begin...");
    exec("INSERT INTO charm(name, description, energy) " +
      "SELECT \"name\", '', 1.0 FROM TMP_TABLE WHERE \"name\" IS NOT NULL AND \"name\" NOT IN (SELECT name FROM charm);", charmTmp);

    exec("INSERT INTO client (surname, name, patronymic, gender, birth_date, charm_id, cia_id) " +
        "SELECT t2.surname, t2.name, t2.patronymic, t2.gender, to_date(t2.birth_date, 'yyyy-MM-dd'), 1, t2.id " +
        "FROM TMP_TABLE t2 " +
        "WHERE t2.error IS NULL ON CONFLICT(cia_id) DO UPDATE SET " +
        "surname=EXCLUDED.surname," +
        "name=EXCLUDED.name," +
        "patronymic=EXCLUDED.patronymic," +
        "gender=EXCLUDED.gender," +
        "birth_date=EXCLUDED.birth_date," +
        "charm_id=1;",
      clientTmp);
    logger.info("migrate main tmp tables to tables end.");
  }

  @Override
  public void fuseChildTmpTables() {
    logger.info("fuse child tmp tables begin...");

    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientAddressTmp);
    exec("SELECT DISTINCT client," +
      "\"type\"," +
      "CAST(NULL AS VARCHAR(255)) AS error, " +
      "split_part(max(street), '#', 2) AS street," +
      "split_part(max(house), '#', 2) AS house," +
      "split_part(max(flat), '#', 2) AS flat " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor " +
      "GROUP BY client, \"type\", error;", clientAddressTmp);

    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientPhoneTmp);
    exec("SELECT DISTINCT client," +
      "\"type\"," +
      "CAST(NULL AS VARCHAR(255)) AS error," +
      "split_part(max(number), '#', 2) AS number " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor " +
      "GROUP BY client, \"type\", error;", clientPhoneTmp);
    logger.info("fuse child tmp tables end.");
  }

  private void findClientId(String tmp) {
    exec("UPDATE TMP_TABLE SET " +
      "client=(SELECT id FROM client WHERE client.cia_id=TMP_TABLE.client LIMIT 1);", tmp);
  }

  @Override
  public void validateChildTmpTables() {
    logger.info("validate child tmp tables begin...");
    findClientId(clientAddressTmp);
    findClientId(clientPhoneTmp);
    validIsClientExits(clientAddressTmp);
    validIsClientExits(clientPhoneTmp);
    logger.info("validate child tmp tables end.");
  }

  private void validIsClientExits(String tmp) {
    exec("UPDATE TMP_TABLE SET error='client not exist'" +
      "WHERE error IS NULL AND client IS NULL;", tmp);
  }

  @Override
  public void migrateChildTmpTablesToTables() {
    logger.info("migrate child tmp tables to tables begin...");
    exec("INSERT INTO client_address(client, type, street, house, flat) " +
      "SELECT client::int, t2.type, t2.street, t2.house, t2.flat FROM TMP_TABLE t2 " +
      "WHERE t2.error IS NULL ON CONFLICT(client, type) DO UPDATE SET " +
      "street=EXCLUDED.street," +
      "house=EXCLUDED.house," +
      "flat=EXCLUDED.flat;", clientAddressTmp);

    exec("INSERT INTO client_phone(client, type, number) " +
      "SELECT client::int, t2.type, t2.number FROM TMP_TABLE t2 " +
      "WHERE t2.error IS NULL ON CONFLICT(client, type) DO UPDATE SET " +
      "number=EXCLUDED.number;", clientPhoneTmp);
    logger.info("migrate child tmp tables to tables end.");
  }

  private void flushWriters() throws IOException {
    charmCsvBw.flush();
    clientCsvBw.flush();
    clientAddressCsvBw.flush();
    clientPhoneCsvBw.flush();
  }

  @Override
  public void deleteTmpTables() {
    logger.info("delete tmp tables to tables begin...");
    deleteTable(clientTmp);
    deleteTable(clientAddressTmp);
    deleteTable(clientPhoneTmp);
    logger.info("delete tmp tables to tables end.");
  }

  @Override
  public void finish() throws IOException {
    logger.info("finish method begin...");

    clientCsvBw.close();
    clientAddressCsvBw.close();
    clientPhoneCsvBw.close();

    clientCsvFile.delete();
    clientAddressCsvFile.delete();
    clientPhoneCsvFile.delete();

    logger.info("finish method end.");
  }

  class XMLHandler extends DefaultHandler {

    private String thisValues = "";
    private TMPClient tmpClient = new TMPClient();
    private TMPClientAddresses tmpClientAddresses = new TMPClientAddresses();
    private TMPClientPhones tmpClientPhones = new TMPClientPhones();

    @Override
    public void startDocument() {
      logger.info("handler start document...");
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
        write(tmpClient, tmpClientAddresses, tmpClientPhones);
      } catch (Exception e) {
        logger.error(e.getMessage());
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
      logger.info("handler end document.");
    }
  }
}