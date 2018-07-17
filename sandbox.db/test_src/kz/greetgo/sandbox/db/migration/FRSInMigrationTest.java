package kz.greetgo.sandbox.db.migration;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.migration.workers.frs.FRSInMigration;
import kz.greetgo.sandbox.db.test.dao.FRSMigrationTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

public class FRSInMigrationTest extends ParentTestNg {

  FRSInMigration frsInMigration = new FRSInMigration();

  public BeanGetter<FRSMigrationTestDao> frsInMigrationBeanGetter;

  @AfterMethod
  public void dropTables() throws Exception {
    frsInMigration.connect();
    frsInMigration.prepareWorker();

    frsInMigrationBeanGetter.get().createTempAccountTable();
    frsInMigrationBeanGetter.get().createTempTransactionTable();

  }

  @BeforeMethod
  public void createTables() throws SQLException {
    frsInMigrationBeanGetter.get().dropAccountTable();
    frsInMigrationBeanGetter.get().dropTransactionTable();

    frsInMigration.closeConnection();
  }


  @Test
  public void testInsertTransactionIntoTemp() {

  }

  @Test
  public void testInsertAccountIntoTemp() {

  }

  @Test
  public void testInsertAccountIntoReal() {

  }

  @Test
  public void testInsertTransactionIntoReal() {

  }
}
