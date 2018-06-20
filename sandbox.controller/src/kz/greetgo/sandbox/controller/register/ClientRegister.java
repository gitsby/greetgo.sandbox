package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.render.ClientRender;

import java.util.List;

/**
 * Работа с клиентами
 */
public interface ClientRegister {

  /**
   * Предоставляет детальную информацию о клиенте
   *
   * @param clientId идентификатор клиента
   * @return детальная информация о клиенте
   */
  Details detail(Integer clientId);

  /**
   * Сохраняет или изменяет информацию о клиенте
   *
   * @param clientToSave моделька для сохранения клиента
   *
   * @return id нового или измененного клиента
   */
  Integer save(ClientToSave clientToSave);

  /**
   * Удалает клиент из списка
   *
   * @param clientId идентификатор клиента
   */
  void delete(Integer clientId);

  /**
   * Предоставляет отфильтрованный список клиентов
   *
   * @param filter параметры фильтров для списка слиентов
   * @return список слиентов после фильтрации
   */
  List<ClientRecord> getRecords(ClientFilter filter);

  /**
   * Предоствовляет количество клиентов с отфильтрованного результата
   *
   * @param filter параметры фильтров для списка слиентов
   * @return количество клиентов после фильтрации
   */
  int getRecordsCount(ClientFilter filter);

  /**
   * Предостволяет список характеров
   *
   * @return список характеров
   */
  List<CharmRecord> getCharms();

  void renderClientList(String name, String author, ClientRender render);
}
