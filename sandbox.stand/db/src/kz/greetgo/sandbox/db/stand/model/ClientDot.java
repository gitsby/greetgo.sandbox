package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.ClientInfo;

public class ClientDot {
  public String name, surname;

  public ClientInfo toClientInfo () {
    ClientInfo clientInfo = new ClientInfo();
    clientInfo.name = name;
    clientInfo.surname = surname;
    return clientInfo;
  }

  public void showInfo() {
    System.out.println("----------: Client with name " + name + " and surname " + surname);
  }
}
