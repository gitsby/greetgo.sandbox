package kz.greetgo.sandbox.db.util;

import kz.greetgo.sandbox.db.configs.ConnectionConfig;
import kz.greetgo.sandbox.db.configs.SSHConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtils {

  public static Connection create(ConnectionConfig connectionConfig) throws Exception {
    return DriverManager.getConnection(connectionConfig.url(), connectionConfig.user(), connectionConfig.password());
  }

  public static ConnectionConfig fileToConnectionConfig(File configFile) throws IOException {
    ConfigData configData = new ConfigData();
    configData.loadFromFile(configFile);
    return new ConnectionConfig() {
      @Override
      public String url() {
        return configData.get("url");
      }

      @Override
      public String user() {
        return configData.get("user");
      }

      @Override
      public String password() {
        return configData.get("password");
      }
    };
  }

  public static SSHConfig fileToSSHConfig(File configFile) throws IOException {
    ConfigData configData = new ConfigData();
    configData.loadFromFile(configFile);
    return new SSHConfig() {
      @Override
      public String host() {
        return configData.get("host");
      }

      @Override
      public String user() {
        return configData.get("user");
      }

      @Override
      public String password() {
        return configData.get("password");
      }

      @Override
      public Integer port() {
        return Integer.parseInt(configData.get("port"));
      }
    };
  }
}
