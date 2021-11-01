/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;

public class Database {

  // Database version
  private static final int VERSION = 2;
  private static final Logger log = getLogger(Database.class);
  private static final String CONNECTION_STR = System.getenv("CONNECTION_STR");
  private static final String DB_USER = System.getenv("USER");
  private static final String DB_PSW = System.getenv("PW");
  private final String url;
  private final String user;
  private final String password;

  public Database(String url, String user, String psw) {
    this.url =
        url == null ? CONNECTION_STR : url;
    this.user = user == null ? DB_USER : user;
    this.password = psw == null ? DB_PSW : psw;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Database() {
    this(null, null, null);
  }

  public static int getVersion() {
    return VERSION;
  }

  public void runMigrations() {
    try {
      int currentVersion = getCurrentVersion();
      while (currentVersion < VERSION) {
        log.warn("Current DB version {} is smaller than expected {}", currentVersion, VERSION);
        runMigration(currentVersion + 1);
        int newVersion = getCurrentVersion();
        if (newVersion > currentVersion) {
          currentVersion = newVersion;
          log.info("Updated database to version {}", newVersion);
        } else {
          log.error("Something went wrong, version not increased to {} ", newVersion);
        }
      }
    } catch (SQLException | IOException e) {
      log.error(e.getMessage());
    }
  }

  private void runMigration(int i) throws IOException, SQLException {
    String migrationFile = String.format("migrate/%d.sql", i);
    log.info("Running migration: {}", migrationFile);
    InputStream stream = Database.class.getClassLoader().getResourceAsStream(migrationFile);
    if (stream == null) {
      log.error("Migration file does not exist: {}", migrationFile);
      throw new FileNotFoundException(migrationFile);
    }
    try (Connection conn = getConnection()) {
      conn.setAutoCommit(false);
      ScriptRunner runner = new ScriptRunner(conn);
      runner.setLogWriter(null);
      runner.setStopOnError(true);
      runner.runScript(new BufferedReader(new InputStreamReader(stream)));
      conn.commit();
    }
    log.info("Migration script completed");
  }

  public int getCurrentVersion() {
    try (Connection conn = getConnection()) {
      ResultSet rs;
      try (Statement s = conn.createStatement()) {
        rs = s.executeQuery("SELECT value FROM properties WHERE name = 'version';");
        if (rs.next()) {
          String column = rs.getString("value");
          return Integer.parseInt(column);
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage());
    }
    return -1;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }
}
