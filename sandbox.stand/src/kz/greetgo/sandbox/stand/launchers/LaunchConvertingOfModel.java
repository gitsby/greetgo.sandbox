package kz.greetgo.sandbox.stand.launchers;

import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.ts_java_convert.ConvertModelBuilder;

import java.io.File;

public class LaunchConvertingOfModel {
  public static void main(String[] args) throws Exception {
    File sourceDir = Modules.clientDir().toPath()
      .resolve("front/ts/model").toFile();
    File destinationDir = Modules.controllerDir().toPath()
      .resolve("src").toFile();
    String destinationPackage = "kz.greetgo.sandbox.controller.model";

    new ConvertModelBuilder()
      .sourceDir(sourceDir)
      .destinationDir(destinationDir)
      .destinationPackage(destinationPackage)
      .create().execute();
  }
}
