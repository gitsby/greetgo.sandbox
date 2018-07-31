package kz.greetgo.sandbox.db.test.beans._develop_;

import kz.greetgo.conf.SysParams;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.db.beans.all.AllConfigFactory;
import kz.greetgo.sandbox.db.configs.DbTmpConfig;
import kz.greetgo.sandbox.db.util.App;
import kz.greetgo.sandbox.db.util.LiquibaseManager;
import kz.greetgo.sandbox.db.util.LiquibaseTmpManager;
import kz.greetgo.util.ServerUtil;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import static kz.greetgo.sandbox.db.test.util.DbUrlUtils.changeUrlDbName;
import static kz.greetgo.sandbox.db.test.util.DbUrlUtils.extractDbName;

@Bean
public class MigrationWorker {
    final Logger logger = Logger.getLogger(getClass());

    public BeanGetter<DbTmpConfig> realDbTmpConfig;
    public BeanGetter<DbTmpConfig> tmpDbTmpConfig;

    public BeanGetter<AllConfigFactory> allPostgresConfigFactory;

    public BeanGetter<LiquibaseTmpManager> liquibaseManager;


    private final java.util.Set<String> alreadyRecreatedUsers = new HashSet<>();
    public Connection connection;

    public void recreateAll() throws Exception {
        prepareDbTmpConfig();
        recreateDb();

        liquibaseManager.get().apply();
        App.do_not_run_liquibase_on_deploy_war().createNewFile();
    }

    
    private void recreateDb() throws Exception {

        final String dbName = extractDbName(realDbTmpConfig.get().url());
        final String username = realDbTmpConfig.get().username();
        final String password = realDbTmpConfig.get().password();



        try (Connection con = getPostgresAdminConnection()) {

            try (Statement stt = con.createStatement()) {
                logger.info("drop database " + dbName);
                stt.execute("drop database " + dbName);
            } catch (PSQLException e) {
                System.err.println(e.getServerErrorMessage());
            }

            if (!alreadyRecreatedUsers.contains(username)) {
                alreadyRecreatedUsers.add(username);

                try (Statement stt = con.createStatement()) {
                    logger.info("drop user " + username);
                    stt.execute("drop user " + username);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    //ignore
                }

                try (Statement stt = con.createStatement()) {
                    logger.info("create user " + username + " encrypted password '" + password + "'");
                    stt.execute("create user " + username + " encrypted password '" + password + "'");
                } catch (PSQLException e) {
                    ServerErrorMessage sem = e.getServerErrorMessage();
                    if ("CreateRole".equals(sem.getRoutine())) {
                        throw new RuntimeException("Невозможно создать пользователя " + username + ". Возможно кто-то" +
                                " приконектился к базе данных под этим пользователем и поэтому он не удаляется." +
                                " Попробуйте разорвать коннект с БД или перезапустить БД. После повторите операцию снова", e);
                    }

                    throw e;
                }
            }

            try (Statement stt = con.createStatement()) {
                logger.info("create database " + dbName);
                stt.execute("create database " + dbName);
            }
            try (Statement stt = con.createStatement()) {
                logger.info("grant all on database " + dbName + " to " + username);
                stt.execute("grant all on database " + dbName + " to " + username);
            }
        }
    }

    public void cleanConfigsForTeamcity() {
        if (System.getProperty("user.name").startsWith("teamcity")) {
            ServerUtil.deleteRecursively(App.appDir());
        }
    }

    private void prepareDbTmpConfig() throws Exception {
        File file = allPostgresConfigFactory.get().storageFileFor(DbTmpConfig.class);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            writeDbTmpConfigFile();
        } else if ("null".equals(realDbTmpConfig.get().url())) {
            writeDbTmpConfigFile();
            allPostgresConfigFactory.get().reset();
        }
    }

    public Connection getTmpDbConnection() throws Exception {
        Class.forName("org.postgresql.Driver");

        this.connection = DriverManager.getConnection(
                realDbTmpConfig.get().url(),
                realDbTmpConfig.get().username(),
                realDbTmpConfig.get().password()
        );
        return  this.connection;

    }


    private void writeDbTmpConfigFile() throws Exception {
        File file = allPostgresConfigFactory.get().storageFileFor(DbTmpConfig.class);
        try (PrintStream out = new PrintStream(file, "UTF-8")) {
            out.println("url=" + changeUrlDbName(SysParams.pgAdminUrl(), System.getProperty("user.name") + "_migration_sandbox"));
            out.println("username=" + System.getProperty("user.name") + "_migration_sandbox");
            out.println("password=111");

        }
    }

    public static Connection getPostgresAdminConnection() throws Exception {
        Class.forName("org.postgresql.Driver");

        return DriverManager.getConnection(
                SysParams.pgAdminUrl(),
                SysParams.pgAdminUserid(),
                SysParams.pgAdminPassword()
        );
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void setAutoCommit(boolean b) throws SQLException {
        this.connection.setAutoCommit(b);
    }
}
