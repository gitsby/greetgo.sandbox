package kz.greetgo.sandbox.db.migration.reader;

import java.sql.SQLException;
import java.util.List;

public class ClientSenderThread extends Thread{

  private ClientProcessor processor;
  private List<ClientFromMigration> clients;

  public ClientSenderThread(ClientProcessor clientProcessor, List<ClientFromMigration> clients){
    this.processor = clientProcessor;
    this.clients = clients;
  }

  public void run(){
    try {
      processor.sendClient(clients);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
