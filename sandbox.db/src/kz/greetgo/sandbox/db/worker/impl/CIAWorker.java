package kz.greetgo.sandbox.db.worker.impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.configs.MigrationConfig;
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
import java.util.List;

public class CIAWorker extends Worker {

  private static Logger logger = Logger.getLogger(CIAWorker.class);

  private XMLReader xmlReader;

  private String clientTmp;
  private String clientAddressTmp;
  private String clientPhoneTmp;

  private File clientCsvFile;
  private File clientAddressCsvFile;
  private File clientPhoneCsvFile;

  private Writer clientCsvBw;
  private Writer clientAddressCsvBw;
  private Writer clientPhoneCsvBw;

  public CIAWorker(List<Connection> connections, InputStream inputStream, MigrationConfig migrationConfig) {
    super(connections, inputStream, migrationConfig);
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
    logger.info("create tmp tables");
    setTmpTableNames();
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (id VARCHAR(255) NOT NULL, error varchar(255), surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birth_date varchar(255), charm VARCHAR(255))", clientTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255) NOT NULL, error varchar(255), type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))", clientAddressTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255) NOT NULL, error varchar(255), type VARCHAR(255), number VARCHAR(255))", clientPhoneTmp);
    logger.info("tmp tables created");
  }

  private void setTmpTableNames() {
    clientTmp = getTmpTableName("cia_migration_client");
    clientAddressTmp = getTmpTableName("cia_migration_client_address");
    clientPhoneTmp = getTmpTableName("cia_migration_client_phone");
  }

  @Override
  public void createCsvFiles() {
    logger.info("create csv files");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    logger.info("csv files created");
  }

  private void createFiles() throws IOException {
    clientCsvFile = createFile(TMP_DIR+"client.csv");
    clientAddressCsvFile = createFile(TMP_DIR+"client_address.csv");
    clientPhoneCsvFile = createFile(TMP_DIR+"client_phone.csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    clientCsvBw = getWriter(clientCsvFile);
    clientAddressCsvBw = new BufferedWriter(new PrintWriter(clientAddressCsvFile));
    clientPhoneCsvBw = new BufferedWriter(new PrintWriter(clientPhoneCsvFile));
  }

  @Override
  public void loadCsvFile() {
    logger.info("begin loading file");
    try {
      xmlReader.parse(new InputSource(inputStream));
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void write(TMPClient tmpClient, TMPClientAddresses tmpClientAddresses, TMPClientPhones tmpClientPhones) throws Exception {
    writeTmpClientCsv(tmpClient);
    writeTmpClientAddressCsv(tmpClient.id, tmpClientAddresses.addressFact);
    writeTmpClientAddressCsv(tmpClient.id, tmpClientAddresses.addressReg);
    for (ClientPhone phone : tmpClientPhones.phones) {
      writeTmpClientPhoneCsv(tmpClient.id, phone);
    }
  }

  private void writeTmpClientCsv(TMPClient client) throws IOException {
    clientCsvBw.write(String.format("%s|\\N|%s|%s|%s|%s|%s|%s\n", client.id, checkStr(client.surname, System.nanoTime()),
      checkStr(client.name, System.nanoTime()), checkStr(client.patronymic, System.nanoTime()),
      checkStr(client.gender, System.nanoTime()), checkStr(client.birthDate, System.nanoTime()),
      checkStr(client.charm, System.nanoTime())));
  }

  private void writeTmpClientAddressCsv(String clientId, ClientAddress address) throws IOException {
    clientAddressCsvBw.write(String.format("%s|\\N|%s|%s|%s|%s\n", clientId, checkStr(address.type.name(), null),
      checkStr(address.street, System.nanoTime()), checkStr(address.house,System.nanoTime()),
      checkStr(address.flat,System.nanoTime())));
  }

  private void writeTmpClientPhoneCsv(String clientId, ClientPhone phone) throws IOException {
    clientPhoneCsvBw.write(String.format("%s|\\N|%s|%s\n", clientId, checkStr(phone.type.name(),null), checkStr(phone.number, null)));
  }

  @Override
  public void loadCsvFilesToTmp(){
    CopyManager copyManager;
    try {
      flushWriters();

      copyManager = new CopyManager((BaseConnection) nextConnection());

      copy(copyManager, clientCsvFile, clientTmp);
      copy(copyManager, clientAddressCsvFile, clientAddressTmp);
      copy(copyManager, clientPhoneCsvFile, clientPhoneTmp);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void flushWriters() throws IOException {
    clientCsvBw.flush();
    clientAddressCsvBw.flush();
    clientPhoneCsvBw.flush();
  }

  @Override
  public void fuseTmpTables() {
    logger.info("fuse tmp tables start");
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT id, "
      +"error,"
      +"split_part(max(surname), '#', 2)    AS surname,"
      +"split_part(max(\"name\"), '#', 2)   AS \"name\","
      +"split_part(max(patronymic), '#', 2) AS patronymic,"
      +"split_part(max(gender), '#', 2)     AS gender,"
      +"split_part(max(birth_date), '#', 2) AS birth_date,"
      +"split_part(max(charm), '#', 2)      AS charm "
      +"INTO TMP_TABLE "
      +"FROM TMP_TABLE_conductor "
      +"GROUP BY id, error;", clientTmp);


    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientAddressTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT client," +
      "\"type\"," +
      "error, " +
      "split_part(max(street), '#', 2) AS street," +
      "split_part(max(house), '#', 2) AS house," +
      "split_part(max(flat), '#', 2) AS flat " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor " +
      "GROUP BY client, \"type\", error;", clientAddressTmp);

    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO TMP_TABLE_conductor", clientPhoneTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT client," +
      "\"type\"," +
      "error," +
      "split_part(max(number), '#', 2) AS number " +
      "INTO TMP_TABLE " +
      "FROM TMP_TABLE_conductor " +
      "GROUP BY client, \"type\", error;", clientPhoneTmp);

    logger.info("fuse tmp tables end");
  }

  private void createIsDateFunction() {
    logger.info("create is_date function");
    //language=PostgreSQL
    exec("CREATE OR REPLACE FUNCTION is_date(s varchar) RETURNS BOOLEAN AS $$ " +
      "BEGIN " +
      " PERFORM s::date;" +
      "   RETURN TRUE;" +
      " EXCEPTION WHEN OTHERS THEN" +
      "   RETURN FALSE;" +
      "END;" +
      "$$ LANGUAGE plpgsql;", null);
  }

  @Override
  public void validateTmpTables() {
    logger.info("validation of tables begin");
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='surname is not defined'" +
      "WHERE error IS NULL AND surname IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='name is not defined'" +
      "WHERE error IS NULL AND \"name\" IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='birth_date is not defined'" +
      "WHERE error IS NULL AND birth_date IS NULL;", clientTmp);

    createIsDateFunction();

    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='birth_date format is wrong'" +
      "WHERE error isnull and not is_date(birth_date)", clientTmp);
    logger.info("validation of tables end");
  }

  @Override
  public void migrateToTables() {
    logger.info("migration of tables begin");
    logger.info("migration of client table begin");
    //language=PostgreSQL
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
    logger.info("migration of client table end");

    findAndCheckClientId(clientAddressTmp);
    findAndCheckClientId(clientPhoneTmp);

    logger.info("migration of client_address table begin");
    //language=PostgreSQL
    exec("INSERT INTO client_address(client, type, street, house, flat) " +
      "SELECT client::int, t2.type, t2.street, t2.house, t2.flat FROM TMP_TABLE t2 " +
      "WHERE t2.error IS NULL ON CONFLICT(client, type) DO UPDATE SET " +
      "street=EXCLUDED.street," +
      "house=EXCLUDED.house," +
      "flat=EXCLUDED.flat;", clientAddressTmp);
    logger.info("migration of client_address table end");


    logger.info("migration of client_phone table begin");
    //language=PostgreSQL
    exec("INSERT INTO client_phone(client, type, number) " +
      "SELECT client::int, t2.type, t2.number FROM TMP_TABLE t2 " +
      "WHERE t2.error IS NULL ON CONFLICT(client, type) DO UPDATE SET " +
      "number=EXCLUDED.number;", clientPhoneTmp);
    logger.info("migration of client_phone table end");
    logger.info("migration of tables end");
  }

  private void findAndCheckClientId(String tmpTable) {
    logger.info(tmpTable+" find client id begin");
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET " +
      "client=(SELECT id FROM client WHERE client.cia_id=TMP_TABLE.client);", tmpTable);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='client not exist'" +
      "WHERE error IS NULL AND client IS NULL;", tmpTable);
    logger.info(tmpTable+" find client id end");
  }

  @Override
  public void deleteTmpTables() {
//    exec("DROP TABLE TMP_TABLE", clientTmp);
//    exec("DROP TABLE TMP_TABLE_conductor", clientTmp);
  }

  @Override
  public void finish() throws IOException {
    logger.info("start of finish method");

    clientCsvBw.close();
    clientAddressCsvBw.close();
    clientPhoneCsvBw.close();

    clientCsvFile.delete();
    clientAddressCsvFile.delete();
    clientPhoneCsvFile.delete();
    logger.info("end of finish method");
  }

  class XMLHandler extends DefaultHandler {

    private String thisValues = "";
    private TMPClient tmpClient = new TMPClient();
    private TMPClientAddresses tmpClientAddresses = new TMPClientAddresses();
    private TMPClientPhones tmpClientPhones = new TMPClientPhones();

    @Override
    public void startDocument() {
      logger.info("handler start document");
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
      logger.info("handler end document");
    }
  }
}