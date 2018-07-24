package kz.greetgo.sandbox.migration.__prepare__.core;

import java.sql.SQLException;

public class ExecFailed extends RuntimeException {
  public ExecFailed(SQLException e) {
    super(e.getMessage(), e);
  }
}
