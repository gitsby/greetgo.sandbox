package kz.greetgo.sandbox.db.util;

public interface LiquibaseManager {
  void apply() throws Exception;
}
