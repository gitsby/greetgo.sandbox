package kz.greetgo.sandbox.db.migration.reader;

import kz.greetgo.sandbox.db.migration.reader.objects.NewAccountFromMigration;
import kz.greetgo.sandbox.db.migration.reader.processors.AccountProcessor;

import java.util.List;

public class AccountSenderThread extends Thread {

  private AccountProcessor processor;
  private List<NewAccountFromMigration> accounts;

  public AccountSenderThread(AccountProcessor processor, List<NewAccountFromMigration> accounts) {
    this.processor = processor;
    this.accounts = accounts;
  }

  public void run() {
    try {
      processor.sendAccounts(accounts);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
