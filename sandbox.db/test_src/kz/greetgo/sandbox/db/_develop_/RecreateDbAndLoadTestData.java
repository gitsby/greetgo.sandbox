package kz.greetgo.sandbox.db._develop_;

import kz.greetgo.sandbox.db.test.util.TestsBeanContainer;
import kz.greetgo.sandbox.db.test.util.TestsBeanContainerCreator;

/**
 * <p>
 * see --> Инициация приложения на рабочем месте разработчика c загрузкой в БД тестовых данных из стэнда
 * </p>
 * <p>
 * Этот скрипт запускается для иницииации приложения: здесь автоматически настраиваются конфиги и инициируется БД
 * </p>
 * <p>
 * Также этот скрипт загружает в БД тестовые данные из стэнда
 * </p>
 */
public class RecreateDbAndLoadTestData {
  public static void main(String[] args) throws Exception {
    new RecreateDbAndLoadTestData().run();
  }

  private void run() throws Exception {
    TestsBeanContainer bc = TestsBeanContainerCreator.create();

    bc.dbWorker().recreateAll();
    bc.dbLoader().loadTestData();
  }
}
