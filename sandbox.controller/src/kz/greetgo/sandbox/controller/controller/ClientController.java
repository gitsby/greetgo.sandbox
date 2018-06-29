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
  @Mapping("/details")
  public ClientDetails details(@Par("clientId") int clientId) {
    return clientRegister.get().detail(clientId);
  }


  @ToJson
  @MethodFilter(RequestMethod.POST)
  @Mapping("/save")
  public Integer save(@Par("clientToSave") @Json ClientToSave client) {
    return clientRegister.get().save(client);
  }

  @ToJson
  @MethodFilter(RequestMethod.DELETE)
  @Mapping("/delete")
  public void delete(@Par("clientId") int clientId) {
    clientRegister.get().delete(clientId);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/list")
  public List<ClientRecord> list(@Par("clientFilter") @Json ClientFilter filter) {
    return clientRegister.get().getRecords(filter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/count")
  public int count(@Par("clientFilter") @Json ClientFilter filter) {
    return clientRegister.get().getRecordsCount(filter);
  }

  @ToJson
  @MethodFilter(RequestMethod.GET)
  @Mapping("/get-charms")
  public List<CharmRecord> charms() { return clientRegister.get().getCharms(); }

}
