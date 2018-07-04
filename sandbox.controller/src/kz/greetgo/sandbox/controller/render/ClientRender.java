package kz.greetgo.sandbox.controller.render;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.Date;

public interface ClientRender {
  void start(String name, Date createdDate);

  void append(ClientRecord asdRow);

  void finish();
}