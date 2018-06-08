package kz.greetgo.sandbox.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.JettyWarServlet;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.sandbox.stand.util.WebAppContextRegistration;

import java.util.ArrayList;
import java.util.List;

@Bean
public class JettyControllersRegistration extends JettyWarServlet implements WebAppContextRegistration {

  public BeanGetter<List<Controller>> controllerList;

  @Override
  protected List<Object> getControllerList() {
    return new ArrayList<>(controllerList.get());
  }

  public BeanGetter<Views> views;

  @Override
  protected Views getViews() {
    return views.get();
  }

  @Override
  protected void afterRegistered() {
    System.err.println("[WebAppContext] --------------------------------------");
    System.err.println("[WebAppContext] -- USING CONTROLLERS:");
    for (TunnelExecutorGetter teg : tunnelExecutorGetters) {
      System.err.println("[WebAppContext] --   " + teg.infoStr());
    }
    System.err.println("[WebAppContext] --------------------------------------");
    printRegistration();
  }

  @Override
  protected String mappingBase() {
    return "/api/*";
  }

  @Override
  protected String getTargetSubContext() {
    return "/api";
  }

  @Override
  public double priority() {
    return 0;
  }
}
