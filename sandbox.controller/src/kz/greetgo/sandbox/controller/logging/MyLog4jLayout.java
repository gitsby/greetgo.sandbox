package kz.greetgo.sandbox.controller.logging;

import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLog4jLayout extends org.apache.log4j.Layout {

  private final StringBuilder buffer = new StringBuilder(255);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  private int loggerNameCut = 2;

  @SuppressWarnings("unused")
  public void setLoggerNameCut(int loggerNameCut) {
    this.loggerNameCut = loggerNameCut;
  }

  @Override
  public String format(LoggingEvent event) {
    StringBuilder buffer = this.buffer;
    buffer.setLength(0);

    buffer.append(dateFormat.format(new Date(event.getTimeStamp()))).append(' ');
    LogIdentity.appendLogIdentity(buffer);
    {
      final String level = event.getLevel().toString();
      int levelLength = level.length();
      if (levelLength == 5) {
        buffer.append(' ').append(level);
      } else if (levelLength == 4) {
        buffer.append("  ").append(level);
      } else {
        buffer.append(' ').append(level);
      }

    }
    appendLoggerName(buffer, event.getLoggerName(), loggerNameCut);
    buffer.append(' ').append(event.getMessage());
    return buffer.append('\n').toString();
  }

  public static void appendLoggerName(StringBuilder buffer, String loggerName, int loggerNameCut) {
    buffer.append(" [");
    if (loggerNameCut <= 0) {
      buffer.append(loggerName);
    } else if (loggerNameCut == 1) {
      final int index1 = loggerName.lastIndexOf('.');
      if (index1 < 0) {
        buffer.append(loggerName);
      } else {
        buffer.append(loggerName, index1 + 1, loggerName.length());
      }
    } else {
      final int index1 = loggerName.lastIndexOf('.');
      if (index1 < 0) {
        buffer.append(loggerName);
      } else {
        final int index2 = loggerName.lastIndexOf('.', index1 - 1);
        if (index2 < 0) {
          buffer.append(loggerName);
        } else {
          buffer.append(loggerName, index2 + 1, loggerName.length());
        }
      }
    }
    buffer.append(']');
  }

  @Override
  public boolean ignoresThrowable() {
    return true;
  }

  @Override
  public void activateOptions() {
  }
}
