package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.util.LiquibaseManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

@Bean
public class LiquibaseManagerImpl implements LiquibaseManager {

  public BeanGetter<DbConfig> dbConfig;

  public BeanGetter<DbConfig> dbConfig2;

  @Override
  public void apply() throws Exception {

    Class.forName("org.postgresql.Driver");

    System.out.println("==================================");
    System.out.println(dbConfig2.get().url());
    System.out.println(dbConfig2.get().username());
    System.out.println(dbConfig2.get().password());
    System.out.println("==================================");


    try (Connection connection = DriverManager.getConnection(
      dbConfig.get().url(),
      dbConfig.get().username(),
      dbConfig.get().password()
    )) {
      Database database = new PostgresDatabase();

      database.setConnection(new JdbcConnection(connection));

      {
        new Liquibase(
          "liquibase/postgres/changelog-master.xml",
          new ClassLoaderResourceAccessor(), database
        ).update("");
      }
    }
  }
}
