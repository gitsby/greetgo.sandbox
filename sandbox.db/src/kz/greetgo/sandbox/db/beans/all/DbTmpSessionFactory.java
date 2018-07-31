package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.db.InTransaction;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.depinject.core.replace.ReplaceWithAnn;
import kz.greetgo.sandbox.db.configs.DbConfig;
import kz.greetgo.sandbox.db.configs.DbTmpConfig;
import kz.greetgo.sandbox.db.util.LocalSessionFactory;
import kz.greetgo.sandbox.db.util.LocalTmpSessionFactory;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Bean
@ReplaceWithAnn(InTransaction.class)
public class DbTmpSessionFactory extends LocalTmpSessionFactory {

    public BeanGetter<DbTmpConfig> dbTmpConfig;


    @Override
    protected DataSource createDataSource() {
        BasicDataSource pool = new BasicDataSource();

        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUrl(dbTmpConfig.get().url());
        pool.setUsername(dbTmpConfig.get().username());
        pool.setPassword(dbTmpConfig.get().password());

        pool.setInitialSize(0);

        return pool;
    }

    @Override
    protected String databaseEnvironmentId() {
        return "DB_OPER";
    }

}
