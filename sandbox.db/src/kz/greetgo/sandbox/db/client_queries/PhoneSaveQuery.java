package kz.greetgo.sandbox.db.client_queries;

import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.model.Phone;

import java.util.ArrayList;

public class PhoneSaveQuery extends DefaultSaveQuery {

  private ClientToSave client;

  public PhoneSaveQuery(ClientToSave client) {
    super(new StringBuilder(), new ArrayList<>());
    this.client = client;
  }

  @Override
  void prepareSql() {
    if (client.addedPhones != null) {
      for (Phone phone : client.addedPhones) {
        sql.append("insert into client_phone(client_id,    number,    type) values(?,?,?);");
        params.add(client.id);
        params.add(phone.number);
        params.add(phone.type);
      }
    }
    if (client.editedPhones != null) {
      for (Phone phone : client.editedPhones) {
        sql.append("update client_phone set number=? where client_id=? and number=?;");
        params.add(phone.editedTo);
        params.add(client.id);
        params.add(phone.number);
      }
    }
    if (client.deletedPhones != null) {
      for (Phone phone : client.deletedPhones) {
        sql.append("update client_phone set actual=0 where client_id=? and number=?;");
        params.add(client.id);
        params.add(phone.number);
      }
    }

  }
}
