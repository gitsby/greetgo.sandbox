package kz.greetgo.sandbox.stand.util;

import java.io.File;
import java.net.URI;

public class FileUtils {
  public static boolean isParent(File parentDir, File childFile) {

    URI parentUri = parentDir.toURI();

    URI childUri = childFile.toURI();

    return !parentUri.relativize(childUri).isAbsolute();
  }
}
