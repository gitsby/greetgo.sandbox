package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.*;

import java.util.List;

/**
 * Работа с клиентами
 */
public interface ClientRegister {

  ClientInfo get(int clientId);

  /**
   * Предоставляет детальную информацию о клиенте
   *
   * @param clientId идентификатор клиента
   * @return детальная информация о клиенте
   */
  Details detail(int clientId);

  /**
   * Сохраняет или изменяет информацию о клиенте
   *
   * @param clientToSave моделька для сохранения клиента
   */
  void save(ClientToSave clientToSave);

  /**
   * Удалает клиент из списка
   *
   * @param clientId идентификатор клиента
   */
  void remove(int clientId);

  List<ClientRecord> getRecords(ClientFilter filter);

  int getRecordsCount(ClientFilter filter);

  List<Charm> getCharms();
}
