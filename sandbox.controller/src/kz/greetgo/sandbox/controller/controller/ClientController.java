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
    return clientRegister.get().save(editedClient);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/get-client-count")
  public int getClientCount(@Json @Par("filter") ClientRecordFilter clientRecordFilter) {
    return clientRegister.get().getClientCount(clientRecordFilter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/get-clients")
  public List<ClientRecord> getClients(
    @Json @Par("filter") ClientRecordFilter philter) {
    return clientRegister.get().getClients(philter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/details")
  public ClientDetails getClientById(@Par("clientId") int clientId) {
    return clientRegister.get().getClientDetails(clientId);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/characters")
  public List<Character> getCharacters() {
    return clientRegister.get().getCharacters();
  }

}
