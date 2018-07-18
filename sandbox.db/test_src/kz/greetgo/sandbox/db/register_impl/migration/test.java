package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.register_impl.MigrationRegisterImpl;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

public class test extends ParentTestNg {
  public BeanGetter<MigrationRegisterImpl> register;

  @Test
  public void test() {
    register.get().start();
  }
}
