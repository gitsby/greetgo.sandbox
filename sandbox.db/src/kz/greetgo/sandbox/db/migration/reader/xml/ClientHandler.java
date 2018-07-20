package kz.greetgo.sandbox.db.migration.reader.xml;

import kz.greetgo.sandbox.db.helper.DateHelper;
import kz.greetgo.sandbox.db.migration.reader.objects.TempAddress;
import kz.greetgo.sandbox.db.migration.reader.objects.TempClient;
import kz.greetgo.sandbox.db.migration.reader.objects.TempPhone;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler extends DefaultHandler {

  private TempClient client;


  private boolean isMobilePhone = false;
  private boolean isWorkPhone = false;
  private boolean isHomePhone = false;

  public static final int CLIENT_BATCH_SIZE = 1000;

  private int threadNum = 0;

  private Connection connection;
  private PreparedStatement clientsStatement;
  private PreparedStatement phoneStatement;
  private PreparedStatement addressStatement;

  private List<TempPhone> phones = new LinkedList<>();

  private List<TempAddress> addresses = new LinkedList<>();

  public ClientHandler(Connection connection, PreparedStatement clientsStatement, PreparedStatement phoneStatement, PreparedStatement addressStatement) {
    this.connection = connection;
    this.clientsStatement = clientsStatement;
    this.phoneStatement = phoneStatement;
    this.addressStatement = addressStatement;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if ("client".equals(qName)) {
      client = new TempClient();
      client.error = "";
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
      TempAddress address = new TempAddress();
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
      TempPhone phone = new TempPhone();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "MOBILE";

      phones.add(phone);
      isMobilePhone = false;
    }
    if (isWorkPhone) {
      TempPhone phone = new TempPhone();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "WORKING";

      phones.add(phone);
      isWorkPhone = false;
    }
    if (isHomePhone) {
      TempPhone phone = new TempPhone();
      phone.number = new String(ch, start, length);
      phone.client_id = client.client_id;
      phone.type = "HOME";

      phones.add(phone);
      isHomePhone = false;

    }
  }

  int batchSize = 0;

  @Override
  public void endElement(String uri, String localName, String qName) {
    if (client != null) {
      if (qName.equals("client")) {
        batchSize++;

        try {
          sendClient();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }

      } else if (qName.equals("cia")) {
        try {
          executeBatches();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private void sendClient() throws SQLException {
    java.sql.Date date = null;

    if (isValidFormat("yyyy-MM-dd", client.birth)) {
      date = formatDate(client.birth);
    }
    batchInsert(clientsStatement, client.client_id, client.name, client.surname, client.patronymic, client.gender, client.charm, date, client.timestamp);

    for (int i = 0; i < addresses.size(); i++) {
      TempAddress address = addresses.get(i);
      batchInsert(addressStatement, address.client_id, address.street, address.house, address.flat, address.type);
    }

    for (int i = 0; i < phones.size(); i++) {
      TempPhone phone = phones.get(i);
      batchInsert(phoneStatement, phone.client_id, phone.number, phone.type);
    }

    if (batchSize > CLIENT_BATCH_SIZE) {
      executeBatches();
    }
    phones = new ArrayList<>();
    addresses = new ArrayList<>();
  }

  private void executeBatches() throws SQLException {
    clientsStatement.executeBatch();
    addressStatement.executeBatch();
    phoneStatement.executeBatch();
    connection.commit();
    batchSize = 0;
  }

  private void batchInsert(PreparedStatement statement, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      statement.setObject(i + 1, params[i]);
    }
    statement.addBatch();
  }

  public java.sql.Date formatDate(String birth) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date date = format.parse(birth);
      return new java.sql.Date(date.getTime());
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isValidFormat(String format, String value) {
    SimpleDateFormat form = new SimpleDateFormat(format);
    Date currentDate = new Date();

    try {
      Date birthDate = form.parse(value);
      int diffYears = DateHelper.calculateAge(DateHelper.toLocalDate(birthDate), DateHelper.toLocalDate(currentDate));

      return ((3 < diffYears) && (diffYears < 1000));
    } catch (Exception e) {
      return false;
    }

  }
}
