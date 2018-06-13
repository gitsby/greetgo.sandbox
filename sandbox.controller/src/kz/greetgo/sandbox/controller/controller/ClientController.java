package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.sandbox.controller.model.Character;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(RequestMethod.DELETE)
  @Mapping("/delete")
  public void delete(@Par("index") int index) {
    clientRegister.get().deleteClient(index);
  }

  @ToJson
  @MethodFilter(RequestMethod.POST)
  @Mapping("/save")
  public ClientRecord save(@Json @Par("editedClient") ClientToSave editedClient) {
    return clientRegister.get().editedClient(editedClient);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/getPaginationNum")
  public int getPaginationNum(@Json @Par("philter") ClientRecordPhilter clientRecordPhilter) {
    return clientRegister.get().getRequestedPaginationNum(clientRecordPhilter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/getClients")
  public List<ClientRecord> getClients(
    @Json @Par("philter") ClientRecordPhilter philter) {
    return clientRegister.get().getClients(philter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/getClientWithId")
  public ClientDetails getClientById(@Par("clientId") int clientId) {
    return clientRegister.get().getClientById(clientId);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/characters")
  public List<Character> getCharacters() {
    return clientRegister.get().getCharacters();
  }

}
