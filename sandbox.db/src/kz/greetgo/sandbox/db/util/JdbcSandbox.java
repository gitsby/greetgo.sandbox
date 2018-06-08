package kz.greetgo.sandbox.db.util;

import kz.greetgo.db.AbstractJdbcWithDataSource;
import kz.greetgo.db.TransactionManager;

import javax.sql.DataSource;

public class JdbcSandbox extends AbstractJdbcWithDataSource {

  private final DataSource dataSource;
  private final TransactionManager transactionManager;

  public JdbcSandbox(DataSource dataSource, TransactionManager transactionManager) {
    this.dataSource = dataSource;
    this.transactionManager = transactionManager;
  }

  @Override
  protected DataSource getDataSource() {
    return dataSource;
  }

  @Override
  protected TransactionManager getTransactionManager() {
    return transactionManager;
  }
}
