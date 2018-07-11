package kz.greetgo.sandbox.db.configs;


import kz.greetgo.conf.hot.DefaultStrValue;

public interface MigrationConfig {

  @DefaultStrValue("/Users/adilbekmailanov/migration.d/tmp")
  String tmpFolder();

  @DefaultStrValue("/Users/tester/migrationFolder")
  String migrationFilesFolder();

}