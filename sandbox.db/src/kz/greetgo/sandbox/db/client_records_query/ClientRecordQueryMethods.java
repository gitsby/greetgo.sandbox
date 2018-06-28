package kz.greetgo.sandbox.db.client_records_query;


import kz.greetgo.db.ConnectionCallback;

public abstract class ClientRecordQueryMethods<T> implements ConnectionCallback<T> {

  // FIXME: 6/28/18 Переименуй метод
  public void all() {
    // FIXME: 6/28/18 Pochemu zdes' selecta net?
    from();
    join();
    leftJoin();
    where();
    orderBy();
    limit();
  }

  abstract void join();

  abstract void leftJoin();

  abstract void where();

  abstract void from();

  abstract void orderBy();

  abstract void limit();
}
