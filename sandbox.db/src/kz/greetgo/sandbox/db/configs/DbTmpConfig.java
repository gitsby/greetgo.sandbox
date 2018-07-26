package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultStrValue;
import kz.greetgo.conf.hot.Description;

@Description("Параметры доступа к тестовому БД (используется только БД Postgresql)")
public interface DbTmpConfig {

    @Description("URL доступа к БД")
    @DefaultStrValue("jdbc:postgres:host:5432/db_name")
    String url();

    @Description("Пользователь для доступа к БД")
    @DefaultStrValue("Some_User")
    String username();

    @Description("Пароль для доступа к БД")
    @DefaultStrValue("Secret")
    String password();
}
