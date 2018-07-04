package kz.greetgo.sandbox.db.register_impl.jdbc.callback;

import kz.greetgo.sandbox.controller.model.ClientFilter;
import kz.greetgo.sandbox.controller.render.ClientRender;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientRenderCallback extends ClientRecordSelectCallback<Void> {

  private final String name;
  private final ClientRender render;

  public ClientRenderCallback(String name, ClientFilter filter, ClientRender render) {
    this.name = name;
    this.filter = filter;
    this.render = render;
  }

  @Override
  public void update() {

  }

  @Override
  public void set() {

  }

  @Override
  public void insert() {

  }

  @Override
  public void values() {

  }

  @Override
  public void orderBy() {

  }

  @Override
  public void offsetAndLimit() {

  }

  @Override
  public void returning() {

  }

  @Override
  public Void run(PreparedStatement ps) throws SQLException {
    try(ResultSet rs = ps.executeQuery()) {
      render.start(name, new Date());
      while (rs.next()) render.append(fromRs(rs));
      render.finish();
    }
    return null;
  }
}
