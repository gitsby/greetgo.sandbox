package kz.greetgo.sandbox.db.configs;

public interface MigrationConfig {
  int uploadMaxBatchSize();
  int downloadMaxBatchSize();
}
