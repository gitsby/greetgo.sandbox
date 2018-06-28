package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.ClientRecordFilter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.IOException;

@Bean
@Mapping("/report")
public class ReportController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @MethodFilter(RequestMethod.GET)
  @Mapping("/render")
  public void render(
    @Json @Par("filter") ClientRecordFilter filter,
    @Par("fileName") String fileName,
    @Par("fileType") String fileType,
    BinResponse binResponse) {

    System.out.println("In report");
    binResponse.setFilename(String.format("%s." + fileType, fileName));
    binResponse.setContentTypeByFilenameExtension();
    try {
      binResponse.out().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    clientRegister.get().renderClientList(filter, "", fileType, binResponse.out());
    binResponse.flushBuffers();
  }

  @Mapping("/test")
  public void test() {
    System.out.println("TESTED!");
  }
}
