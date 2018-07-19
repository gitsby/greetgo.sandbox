package kz.greetgo.sandbox.db.migration.reader.xml;

import kz.greetgo.sandbox.db.migration.reader.json.JSONManager;
import kz.greetgo.sandbox.db.migration.reader.processors.AddressProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.ClientProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.PhoneProcessor;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class XMLManager {

  String filePath;

  ClientHandler handler;


  public XMLManager(String file) {
    filePath = file;
  }

  public void load(ClientProcessor processor, AddressProcessor addressProcessor, PhoneProcessor phoneProcessor) throws ParserConfigurationException, SAXException, IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();

    SAXParser parser = factory.newSAXParser();
    File file = new File(filePath);
    handler = new ClientHandler(processor, addressProcessor, phoneProcessor);
    parser.parse(file, handler);
  }

  public boolean isDone() {
    return false;
  }

  public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, SQLException {
    JSONManager manager = new JSONManager("C:\\Programs\\Web\\Greetgo\\from_frs_10000007.txt");
    System.out.println("LOADING");
    Connection connection;
    Properties properties = new Properties();
    properties.setProperty("user", "kayne_sandbox");
    properties.setProperty("password", "111");
    connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kayne_sandbox", properties);
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    connection.setAutoCommit(false);

    CIAInMigration ciaInMigration = new CIAInMigration(connection);
    ciaInMigration.prepareWorker();
    ciaInMigration.createTempTables();

    XMLManager xmlManager = new XMLManager("C:\\Programs\\Web\\Greetgo\\from_100000.xml");
    final int[] countTimes = {0};
    long startTime = System.nanoTime();
    xmlManager.load(clients -> {
        ciaInMigration.sendClient(clients);
      }, address -> ciaInMigration.sendAddresses(address)
      , phonesFromMigration -> {
        ciaInMigration.sendPhones(phonesFromMigration);
      });
    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;
    System.out.println("Counted times:" + countTimes[0]);
    connection.close();

    System.out.println(totalTime / 1000000000.0);
  }
}
