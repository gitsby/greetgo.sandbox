package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.db.beans.all.BeanConfigAll;
import kz.greetgo.sandbox.server.beans.BeanConfigServer;

@BeanConfig
@Include({BeanConfigServer.class, BeanConfigAll.class})
public class BeanConfigApplication {}
