package kz.greetgo.sandbox.db.migration.reader.xml;

import kz.greetgo.sandbox.db.migration.reader.processors.AddressProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.ClientProcessor;
import kz.greetgo.sandbox.db.migration.reader.processors.PhoneProcessor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

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

  public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
    XMLManager xmlManager = new XMLManager("C:\\Programs\\Web\\Greetgo\\from_300.xml");
    final int[] countTimes = {0};
    long startTime = System.nanoTime();
    xmlManager.load(clients -> {
        countTimes[0]++;
        System.out.println(clients.toString());
      }, address -> System.out.println("ADDRESS HERE")
      , phonesFromMigration -> {
        System.out.println("Phones:" + phonesFromMigration.size());
      });
    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;
    System.out.println("Counted times:" + countTimes[0]);
    System.out.println(totalTime / 1000000000.0);
  }
}
