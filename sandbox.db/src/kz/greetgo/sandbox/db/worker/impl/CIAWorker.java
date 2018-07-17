package kz.greetgo.sandbox.db.worker.impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.db.worker.Worker;
import org.apache.log4j.Logger;
import org.fest.util.Lists;
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

@Bean
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
      logger.error(e);
    }
    xmlReader.setContentHandler(new XMLHandler());
  }

  @Override
  public void fillTmpTables() {
    createTmpTables();
    createCsvFiles();
    loadCsvFile();
    loadCsvFilesToTmpTables();
  }

  @Override
  public void margeTmpTables() {
    parallelTasks(
      this::margeClientTmpTable,
      this::margeClientAddressTmpTable,
      this::margeClientPhoneTmpTable
    );
  }

  @Override
  public void validTmpTables() {
    logger.info("validate main tmp tables begin...");
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.CIA.SURNAME_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND surname IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.CIA.NAME_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND \"name\" IS NULL;", clientTmp);
    //language=PostgreSQL
    exec("UPDATE TMP_TABLE SET error='"+MigrationError.CIA.BIRTH_DATE_NOT_FOUND+'\'' +
      "WHERE error IS NULL AND birth_date IS NULL;", clientTmp);
    logger.info("validate main tmp tables end.");
  }

  @Override
  public void migrateTmpTables() {
    migrateParentTmpTableToTables();
    migrateChildTmpTablesToTables();
  }

  private void createTmpTables() {
    logger.info("create tmp tables begin...");
    setTmpTableNames();
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (name VARCHAR(255))", charmTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (id VARCHAR(255), error VARCHAR(255), surname VARCHAR(255), name VARCHAR(255), patronymic VARCHAR(255), gender VARCHAR(255), birth_date varchar(255), charm VARCHAR(255))", clientTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255), error VARCHAR(255), type VARCHAR(255), street VARCHAR(255), house VARCHAR(255), flat VARCHAR(255))", clientAddressTmp);
    //language=PostgreSQL
    exec("CREATE TABLE TMP_TABLE (client VARCHAR(255), error VARCHAR(255), type VARCHAR(255), number VARCHAR(255))", clientPhoneTmp);
    logger.info("create tmp tables end.");
  }

  private void setTmpTableNames() {
    setTmpTableNames(
      getNameWithDate("cia_migration_client"),
      getNameWithDate("cia_migration_client_address"),
      getNameWithDate("cia_migration_client_phone"));
  }

  public void setTmpTableNames(String clientTmp, String clientAddressTmp, String clientPhoneTmp) {
    this.charmTmp = getNameWithDate("cia_migration_charm");
    this.clientTmp = clientTmp;
    this.clientAddressTmp = clientAddressTmp;
    this.clientPhoneTmp = clientPhoneTmp;
  }

  private void createCsvFiles() {
    logger.info("create csv files begin...");
    try {
      createFiles();
      createWriters();
    } catch (IOException e) {
      logger.error(e);
    }
    logger.info("create csv files end.");
  }

  private void createFiles() throws IOException {
    charmCsvFile = createFile(getNameWithDate("charm")+".csv");
    clientCsvFile = createFile(getNameWithDate("client")+".csv");
    clientAddressCsvFile = createFile(getNameWithDate("client_address")+".csv");
    clientPhoneCsvFile = createFile(getNameWithDate("client_phone")+".csv");
  }

  private void createWriters() throws FileNotFoundException, UnsupportedEncodingException {
    charmCsvBw = getWriter(charmCsvFile);
    clientCsvBw = getWriter(clientCsvFile);
    clientAddressCsvBw = getWriter(clientAddressCsvFile);
    clientPhoneCsvBw = getWriter(clientPhoneCsvFile);
  }

  private void loadCsvFile() {
    logger.info("load csv files begin...");
    try {
      xmlReader.parse(new InputSource(inputStream));
    } catch (Exception e) {
      logger.error(e);
    }
    logger.info("load csv files end.");
  }

  private void write(TMPClient tmpClient, List<TMPClientAddress> tmpClientAddresses, List<TMPClientPhone> tmpClientPhones) {
    try {
      writeTmpCharmCsv(tmpClient.charm);
      writeTmpClientCsv(tmpClient);
      for (TMPClientAddress tmpClientAddress : tmpClientAddresses) {
        tmpClientAddress.client = tmpClient.id;
        writeTmpClientAddressCsv(tmpClientAddress);
      }
      for (TMPClientPhone phone : tmpClientPhones) {
        phone.client = tmpClient.id;
        writeTmpClientPhoneCsv(phone);
      }
    } catch (IOException e) {
      logger.error(e);
    }
  }

  private void writeTmpCharmCsv(String name) throws IOException {
    charmCsvBw.write(name+"\n");
  }

  private void writeTmpClientCsv(TMPClient client) throws IOException {
    if (!isDate(client.birthDate)) client.birthDate = null;
    clientCsvBw.write(String.format("%s|\\N|%s|%s|%s|%s|%s|%s\n", client.id, isNull(client.surname),
      isNull(client.name), isNull(client.patronymic),
      isNull(client.gender), isNull(client.birthDate),
      isNull(client.charm)));
  }

  private void writeTmpClientAddressCsv(TMPClientAddress address) throws IOException {
    clientAddressCsvBw.write(String.format("%s|\\N|%s|%s|%s|%s\n", address.client, isNull(address.type.name()),
      isNull(address.street), isNull(address.house),
      isNull(address.flat)));
  }

  private void writeTmpClientPhoneCsv(TMPClientPhone phone) throws IOException {
    clientPhoneCsvBw.write(String.format("%s|\\N|%s|%s\n", phone.client, isNull(phone.type), isNull(phone.number)));
  }

  private void loadCsvFilesToTmpTables(){
    logger.info("load csv files to tmp tables begin...");
    CopyManager copyManager;
    try {
      flushWriters();

      copyManager = new CopyManager((BaseConnection) connection);

      List<Thread> threadList = Lists.newArrayList();
      threadList.add(new Thread(()->copy(copyManager, charmCsvFile, charmTmp)));
      threadList.add(new Thread(()->copy(copyManager, clientCsvFile, clientTmp)));
      threadList.add(new Thread(()->copy(copyManager, clientAddressCsvFile, clientAddressTmp)));
      threadList.add(new Thread(()->copy(copyManager, clientPhoneCsvFile, clientPhoneTmp)));

      threadList.forEach(Thread::start);
      for (Thread thread : threadList) thread.join();
    } catch (Exception e) {
      logger.error(e);
    }
    logger.info("load csv files to tmp tables end.");
  }

  public void margeClientTmpTable() {
    logger.info("marge main tmp tables begin...");
    if (charmTmp != null) {
      //language=PostgreSQL
      exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", charmTmp);
      //language=PostgreSQL
      exec("SELECT DISTINCT name INTO TMP_TABLE FROM conductor_TMP_TABLE GROUP BY name;", charmTmp);
      //language=PostgreSQL
      exec("DROP TABLE conductor_TMP_TABLE", charmTmp);
    }

    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", clientTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT " +
      "  id, " +
      "  error, " +
      "  FIRST_VALUE(surname) OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN surname IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS surname, " +
      "  FIRST_VALUE(\"name\") OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN \"name\" IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS name, " +
      "  FIRST_VALUE(patronymic) OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN patronymic IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS patronymic, " +
      "  FIRST_VALUE(gender) OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN gender IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS gender, " +
      "  FIRST_VALUE(birth_date) OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN birth_date IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS birth_date, " +
      "  FIRST_VALUE(charm) OVER " +
      "    ( PARTITION BY id " +
      "    ORDER BY CASE WHEN charm IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS charm " +
      "INTO TMP_TABLE FROM conductor_TMP_TABLE", clientTmp);
    //language=PostgreSQL
    exec("DROP TABLE conductor_TMP_TABLE", clientTmp);

    logger.info("marge main tmp tables end.");
  }

  private void margeClientAddressTmpTable() {
    logger.info("marge child tmp tables begin...");
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", clientAddressTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT " +
      "  client, \"type\", " +
      "  error," +
      "  FIRST_VALUE(street) OVER " +
      "    ( PARTITION BY client, \"type\" " +
      "    ORDER BY CASE WHEN street IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS street, " +
      "  FIRST_VALUE(house) OVER " +
      "    ( PARTITION BY client,\"type\" " +
      "    ORDER BY CASE WHEN house IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS house, " +
      "  FIRST_VALUE(flat) OVER " +
      "    ( PARTITION BY client, \"type\" " +
      "    ORDER BY CASE WHEN flat IS NULL " +
      "      THEN NULL " +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS flat " +
      "INTO TMP_TABLE " +
      "FROM conductor_TMP_TABLE;", clientAddressTmp);
    //language=PostgreSQL
    exec("DROP TABLE conductor_TMP_TABLE", clientAddressTmp);
    logger.info("fuse child tmp tables end.");
  }

  private void margeClientPhoneTmpTable() {
    //language=PostgreSQL
    exec("ALTER TABLE TMP_TABLE RENAME TO conductor_TMP_TABLE", clientPhoneTmp);
    //language=PostgreSQL
    exec("SELECT DISTINCT " +
      "  client, \"type\", error, " +
      "  FIRST_VALUE(number) OVER " +
      "    ( PARTITION BY client, type " +
      "    ORDER BY CASE WHEN number IS NULL " +
      "      THEN NULL\n" +
      "             ELSE timeofday() " +
      "             END DESC NULLS LAST " +
      "    ) AS number " +
      "INTO TMP_TABLE " +
      "FROM conductor_TMP_TABLE;", clientPhoneTmp);
    //language=PostgreSQL
    exec("DROP TABLE conductor_TMP_TABLE", clientPhoneTmp);
  }

  private void migrateParentTmpTableToTables() {
    logger.info("migrate main tmp tables to tables begin...");
    //language=PostgreSQL
    exec("INSERT INTO charm(name, description, energy) " +
      "SELECT \"name\", '', 1.0 FROM TMP_TABLE WHERE \"name\" IS NOT NULL AND \"name\" NOT IN (SELECT name FROM charm);", charmTmp);

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
        "charm_id=1," +
        "cia_id=EXCLUDED.cia_id;",
      clientTmp);
    logger.info("migrate main tmp tables to tables end.");
  }

  private void migrateChildTmpTablesToTables() {
    logger.info("migrate child tmp tables to tables begin...");
    //language=PostgreSQL
    exec("INSERT INTO client_address(client, type, street, house, flat) " +
      "SELECT id, t2.type, t2.street, t2.house, t2.flat FROM TMP_TABLE t2 " +
      "INNER JOIN client ON t2.client=client.cia_id " +
      "WHERE t2.error IS NULL ON CONFLICT(client, type) DO UPDATE SET " +
      "street=EXCLUDED.street," +
      "house=EXCLUDED.house," +
      "flat=EXCLUDED.flat;", clientAddressTmp);

    //language=PostgreSQL
    exec("INSERT INTO client_phone(client, type, number) " +
      "SELECT id, t2.type, t2.number FROM TMP_TABLE t2 " +
      "INNER JOIN client ON client.cia_id=t2.client " +
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
    //language=PostgreSQL
    exec("DROP TABLE TMP_TABLE", clientTmp);
    //language=PostgreSQL
    exec("DROP TABLE TMP_TABLE", clientAddressTmp);
    //language=PostgreSQL
    exec("DROP TABLE TMP_TABLE", clientPhoneTmp);
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
    private List<TMPClientAddress> tmpClientAddresses = Lists.newArrayList();
    private List<TMPClientPhone> tmpClientPhones = Lists.newArrayList();

    @Override
    public void startDocument() {
      logger.info("handler start document...");
      tmpClient = new TMPClient();
      tmpClientAddresses = Lists.newArrayList();
      tmpClientPhones = Lists.newArrayList();
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
          tmpClientAddresses.add(getAddress(attributes, AddressTypeEnum.FACT));
          break;
        case "register":
          tmpClientAddresses.add(getAddress(attributes, AddressTypeEnum.REG));
      }
    }

    private TMPClientAddress getAddress(Attributes attributes, AddressTypeEnum typeEnum) {
      TMPClientAddress tmpClientAddress = new TMPClientAddress();
      tmpClientAddress.type = typeEnum;
      tmpClientAddress.street = attributes.getValue("street");
      tmpClientAddress.house = attributes.getValue("house");
      tmpClientAddress.flat = attributes.getValue("flat");
      return tmpClientAddress;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("client")) upload();
      thisValues = "";
    }

    private void upload() {
      write(tmpClient, tmpClientAddresses, tmpClientPhones);

      tmpClient = new TMPClient();
      tmpClientAddresses = Lists.newArrayList();
      tmpClientPhones = Lists.newArrayList();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      switch (thisValues) {
        case "homePhone":
          tmpClientPhones.add(new TMPClientPhone(null,PhoneType.HOME.name(),new String(ch, start, length)));
          break;
        case "workPhone":
          tmpClientPhones.add(new TMPClientPhone(null,PhoneType.WORK.name(),new String(ch, start, length)));
          break;
        case "mobilePhone":
          tmpClientPhones.add(new TMPClientPhone(null,PhoneType.MOBILE.name(),new String(ch, start, length)));
      }
    }

    @Override
    public void endDocument() {
      logger.info("handler end document.");
    }
  }
}