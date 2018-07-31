package kz.greetgo.sandbox.db.beans.all;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.util.AbstractMybatisDaoImplFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

@Bean
public class DaoTmpImplFactory extends AbstractMybatisDaoImplFactory {

    public BeanGetter<DbTmpSessionFactory> dbTmpSessionFactory;


    @Override
    protected SqlSession getSqlSession() {
        return dbTmpSessionFactory.get().sqlSession();
    }

    @Override
    protected Configuration getConfiguration() {
        return dbTmpSessionFactory.get().getConfiguration();
    }
}
