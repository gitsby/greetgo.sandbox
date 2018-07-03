package kz.greetgo.sandbox.db.util;

import java.io.File;

public class ConfigFiles {

  private static File configFile(String name) {
    return new File(System.getProperty("user.home") + "/migration.d/" + name);
  }

  public static File db() {
    return configFile("db.properties");
  }

  public static File ssh() { return configFile("ssh.properties"); }

  public static File tmpDir() { return new File("/Users/adilbekmailanov/migration.d/tmp"); }
}