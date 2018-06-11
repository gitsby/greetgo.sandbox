package kz.greetgo.sandbox.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.stand.util.WebAppContextRegistration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.Comparator;
import java.util.List;

@Bean
public class StandServer implements HasAfterInject {
  public final Server server = new Server(13_13/* Reptilians is out of there */);

  public StandServer start() throws Exception {
    server.start();
    System.err.println("[[[                                ]]]");
    System.err.println("[[[ Stand server has been launched ]]]");
    System.err.println("[[[                                ]]]");
    return this;
  }

  public void join() throws InterruptedException {
    server.join();
  }

  public BeanGetter<List<WebAppContextRegistration>> webAppContextRegistrations;

  @Override
  public void afterInject() throws Exception {
    WebAppContext webAppServlet = new WebAppContext(
      Modules.clientDir().toPath().resolve(".").toString(),
      "/access");

    webAppContextRegistrations.get().stream()
      .sorted(Comparator.comparingDouble(WebAppContextRegistration::priority))
      .forEachOrdered(r -> r.registerTo(webAppServlet));

    server.setHandler(webAppServlet);
  }
}
