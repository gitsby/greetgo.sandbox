package kz.greetgo.sandbox.db.migration.reader;

import kz.greetgo.sandbox.db.migration.reader.objects.AddressFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.AddressProcessor;

import java.sql.SQLException;
import java.util.List;

public class AddressSenderThread extends Thread {

  private AddressProcessor processor;
  private List<AddressFromMigration> addresses;

  public AddressSenderThread(AddressProcessor processor, List<AddressFromMigration> addresses){
    this.processor = processor;
    this.addresses = addresses;
  }

  public void run(){
    try {
      processor.sendAddresses(addresses);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
