package kz.greetgo.sandbox.controller.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Log4jTraceLayout extends Layout {

  private final StringBuilder buf = new StringBuilder(255);

  @Override
  public String format(LoggingEvent loggingEvent) {
    StringBuilder buf = this.buf;
    buf.setLength(0);

    String dat = timeStampLayout(loggingEvent.timeStamp);
    buf.append(dat).append(' ');
    LogIdentity.appendLogIdentity(buf);

    buf.append(' ');

    {
      String loggerName = loggingEvent.getLoggerName();
      if (loggerName != null) {
        int idx = loggerName.lastIndexOf('.');
        if (idx < 0 || idx == loggerName.length() - 1) {
          loggerName = null;
        } else {
          loggerName = loggerName.substring(idx + 1);
        }
        if (loggerName != null) {
          buf.append('[').append(loggerName).append("] ");
        }
      }
    }

    buf.append(loggingEvent.getMessage());

    return buf.append('\n').toString();
  }

  public static String timeStampLayout(long timeStamp) {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date(timeStamp));
  }

  @Override
  public boolean ignoresThrowable() {
    return true;
  }

  @Override
  public void activateOptions() {
  }
}
