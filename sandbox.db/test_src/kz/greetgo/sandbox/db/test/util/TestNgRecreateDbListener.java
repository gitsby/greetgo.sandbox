package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.sandbox.db.test.beans._develop_.DbWorker;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestNgRecreateDbListener implements ISuiteListener {
  @Override
  public void onFinish(ISuite suite) {}

  @Override
  public void onStart(ISuite suite) {
    if (!"All StandDb Test Suite".equals(suite.getName())) return;

    try {
      prepareDatabases();
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }
  }

  private void prepareDatabases() throws Exception {
    {
      DbWorker postgresDbWorker = TestsBeanContainerCreator.create().dbWorker();
      postgresDbWorker.cleanConfigsForTeamcity();
      postgresDbWorker.recreateAll();
    }
  }
}
