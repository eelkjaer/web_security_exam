/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  private static final String CONNECTION_STR = System.getenv("CONNECTION_STR");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PSW = System.getenv("DB_PW");

  public Database() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(CONNECTION_STR, DB_USER, DB_PSW);
  }
}
