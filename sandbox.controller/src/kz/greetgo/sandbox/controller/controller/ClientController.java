package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;


@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/detail")
  public ClientDetail detail(@Par("clientId") int clientId) {
    return clientRegister.get().get(clientId);
  }


  @ToJson
  @MethodFilter(RequestMethod.POST)
  @Mapping("/save")
  public void create(@Par("clientToSave") @Json ClientToSave client) {
    clientRegister.get().save(client);
  }

  @ToJson
  @MethodFilter(RequestMethod.DELETE)
  @Mapping("/remove")
  public void remove(@Par("clientId") int clientId) {
    clientRegister.get().remove(clientId);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/records")
  public List<ClientRecords> records(@Par("clientFilter") @Json ClientFilter filter) {
    return clientRegister.get().getRecords(filter);
  }


  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/recordsCount")
  public int recordsCount(@Par("clientFilter") @Json ClientFilter filter) {
    return clientRegister.get().getRecordsCount(filter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/getCharms")
  public List<Charm> charms() { return clientRegister.get().getCharms(); }
}
