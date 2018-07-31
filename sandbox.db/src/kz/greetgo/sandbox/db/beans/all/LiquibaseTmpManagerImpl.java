package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.DbTmpConfig;
import kz.greetgo.sandbox.db.util.LiquibaseTmpManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

@Bean
public class LiquibaseTmpManagerImpl implements LiquibaseTmpManager {

    public BeanGetter<DbTmpConfig> dbTmpConfig;

    @Override
    public void apply() throws Exception {

        Class.forName("org.postgresql.Driver");

//        dbTmpConfig.get().url();
        try (Connection connection = DriverManager.getConnection(
                dbTmpConfig.get().url(),
                dbTmpConfig.get().username(),
                dbTmpConfig.get().password()
        )) {
            Database database = new PostgresDatabase();

            database.setConnection(new JdbcConnection(connection));

            {
                new Liquibase(
                        "liquibase/postgres/migration.xml",
                        new ClassLoaderResourceAccessor(), database
                ).update("");
            }
        }
    }

    static public void main(String[] args) throws Exception {
        LiquibaseTmpManager lbtm = new LiquibaseTmpManagerImpl();
        lbtm.apply();
    }
}
