package kz.greetgo.sandbox.db.migration.reader;

import java.sql.SQLException;
import java.util.List;

public class PhoneSenderThread extends Thread {

  private PhoneProcessor processor;
  private List<PhoneFromMigration> phones;


  public PhoneSenderThread(PhoneProcessor phoneProcessor, List<PhoneFromMigration> phones){
    this.processor = phoneProcessor;
    this.phones = phones;
  }

  public void run(){
    try {
      processor.sendPhones(phones);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
