package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.depinject.core.BeanContainer;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.db.test.beans._develop_.DbLoader;
import kz.greetgo.sandbox.db.test.beans._develop_.DbWorker;
import kz.greetgo.sandbox.db.test.beans._develop_.MigrationWorker;

@Include(BeanConfigTests.class)
public interface TestsBeanContainer extends BeanContainer {
  DbWorker dbWorker();

  MigrationWorker migrationWorker();

  DbLoader dbLoader();
}
