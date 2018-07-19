package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.model.ClientToSave;
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
  public List<ClientRecord> getClients(@Json @Par("filter") ClientRecordFilter philter) {
    return clientRegister.get().getClients(philter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/details")
  public ClientDetails details(@Par("clientId") int clientId) {
    return clientRegister.get().details(clientId);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/charm")
  public List<CharmRecord> getCharms() {
    return clientRegister.get().getCharms();
  }

}
