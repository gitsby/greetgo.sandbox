package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.BeanConfig;
import kz.greetgo.depinject.core.BeanScanner;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.controller.controller.BeanConfigControllers;
import kz.greetgo.sandbox.db.core.BeanConfigCore;
import kz.greetgo.sandbox.db.dao.postgres.BeanConfigPostgresDao;
import kz.greetgo.sandbox.db.register_impl.BeanConfigRegisterImpl;
import kz.greetgo.sandbox.db.util.BeanConfigUtil;

@BeanConfig
@BeanScanner
@Include({BeanConfigRegisterImpl.class, BeanConfigPostgresDao.class,
  BeanConfigControllers.class, BeanConfigCore.class, BeanConfigUtil.class})
public class BeanConfigAll {}