package kz.greetgo.sandbox.db.migration.reader.xml;

import kz.greetgo.sandbox.db.migration.reader.json.JSONManager;
import kz.greetgo.sandbox.db.migration.workers.cia.CIAInMigrationWorker;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigrationWorker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

public class XMLManager {

  String filePath;

  ClientHandler handler;


  public XMLManager(String file) {
    filePath = file;
  }

  public void load(Connection connection, PreparedStatement clientsStatement, PreparedStatement phoneStatement, PreparedStatement addressStatement) throws ParserConfigurationException, SAXException, IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();

    SAXParser parser = factory.newSAXParser();
    File file = new File(filePath);
    handler = new ClientHandler(connection, clientsStatement, phoneStatement, addressStatement);
    parser.parse(file, handler);
  }

  public boolean isDone() {
    return false;
  }

}
