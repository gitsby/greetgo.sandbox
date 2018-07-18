package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrationRegisterTestImpl extends ParentTestNg {

  public BeanGetter<MigrationRegisterImpl> migrationRegister;

  @Test
  public void testDownload() {
    assertThat(true);
  }

  @Test
  public void testUnpack() {

  }

  @Test
  public void testMigrateCIA() {

  }

  @Test
  public void testMigrateFRS() {

  }
}
