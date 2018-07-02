package kz.greetgo.sandbox.db.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Callback {
  void doSomething(PreparedStatement preparedStatement) throws SQLException;
}
