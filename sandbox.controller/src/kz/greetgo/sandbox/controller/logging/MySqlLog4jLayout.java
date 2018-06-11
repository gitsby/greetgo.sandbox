package kz.greetgo.sandbox.controller.logging;

import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySqlLog4jLayout extends org.apache.log4j.Layout {

  private final StringBuilder buffer = new StringBuilder(255);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  @Override
  public String format(LoggingEvent event) {
    StringBuilder buffer = this.buffer;

    buffer.setLength(0);
    buffer.append(dateFormat.format(new Date(event.getTimeStamp()))).append(' ');
    LogIdentity.appendLogIdentity(buffer);

    {
      buffer.append(" [");
      String loggerName = event.getLoggerName();
      final int index1 = loggerName.lastIndexOf('.');
      if (0 > index1) {
        buffer.append(loggerName);
      } else {
        final int index2 = loggerName.lastIndexOf('.', index1 - 1);
        if (index2 < 0) {
          buffer.append(loggerName);
        } else {
          buffer.append(loggerName, index2 + 1, loggerName.length());
        }
      }
      buffer.append(']');
    }

    buffer.append(" SQL ").append(event.getMessage());

    return buffer.append('\n').toString();
  }

  @Override
  public boolean ignoresThrowable() {
    return true;
  }

  @Override
  public void activateOptions() {
  }
}
