package kz.greetgo.sandbox.db.callbacks;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientReportCallback implements ConnectionCallback<Void> {
  private String contractName;
  private String author;
  private ClientRender render;

  public ClientReportCallback(String contractName, String author, ClientRender render) {
    this.contractName = contractName;
    this.author = author;
    this.render = render;
  }

  @Override
  public Void doInConnection(Connection con) throws Exception {

    StringBuilder sql = new StringBuilder();
    appendSelect(sql);
    appendFrom(sql);
    appendGroupBy(sql);

    PreparedStatement ps = con.prepareStatement(sql.toString());

    try {

      ResultSet rs = ps.executeQuery();

      try {

        render.start(contractName, new Date());

        while (rs.next()) {

          render.append(extract(rs));

        }

        render.finish(author);

      } finally {
        rs.close();
      }


    } finally {
      ps.close();
    }

    return null;
  }

  private void appendSelect(StringBuilder sql) {
    sql.append("SELECT m.id, surname, name, patronymic, DATE_PART('year', '2012-01-01'::date) - DATE_PART('year', '2011-10-02'::date) AS age, AVG(money) AS middle_balance, MAX(money) AS max_balance, MIN(money) AS min_balance ");
  }

  private void appendFrom(StringBuilder sql) {
    sql.append("FROM client m ");
    sql.append("LEFT JOIN client_account x1 ON x1.client=m.id AND m.actual=1 ");
  }

  private void appendGroupBy(StringBuilder sql) {
    sql.append("GROUP BY m.id, m.surname, m.name, m.patronymic ");
  }

  private ClientRow extract(ResultSet rs) throws SQLException {
    ClientRow row = new ClientRow();
    row.id = rs.getInt("id");
    row.surname = rs.getString("surname");
    row.name = rs.getString("name");
    row.patronymic = rs.getString("patronymic");
    row.age = rs.getInt("age");
    row.middle_balance = rs.getInt("middle_balance");
    row.max_balance = rs.getInt("max_balance");
    row.min_balance = rs.getInt("min_balance");
    return row;
  }


}
