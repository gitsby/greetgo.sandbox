package kz.greetgo.sandbox.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Informative {

  public void info(String message) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    System.out.println(sdf.format(new Date()) + " [" + getClass().getSimpleName() + "] " + message);
  }
}
