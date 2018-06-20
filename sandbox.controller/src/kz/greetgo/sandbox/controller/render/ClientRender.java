package kz.greetgo.sandbox.controller.render;

import kz.greetgo.sandbox.controller.render.model.ClientRow;

import java.util.Date;

public interface ClientRender {
  void start(String name, Date createdDate);

  void append(ClientRow asdRow);

  void finish(String authorName);
}