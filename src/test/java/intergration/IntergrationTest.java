/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package intergration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import api.Api;
import domain.user.User;
import domain.user.User.Role;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import infrastructure.DBUser;
import infrastructure.Database;
import infrastructure.GoogleAuthService;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import jdk.jfr.Description;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;

@Tag("integration-test")
@DisplayName("Integration test")
@Description("Integration test of selected user stories")
@TestMethodOrder(MethodOrderer.MethodName.class)
class IntergrationTest {

  private static final Logger log = getLogger(IntergrationTest.class);
  private static Api api;

  static User user = null;

  /**
   * Before you run this integration test, create a user 'cupcaketest' and grant access to the
   * database: You can use the following script:
   * <pre>
   * DROP USER IF EXISTS cupcaketest@localhost;
   * CREATE USER cupcaketest@localhost;
   * GRANT ALL PRIVILEGES ON cupcaketest.* TO cupcaketest@localhost;
   * </pre>
   */
  static void resetTestDatabase() {
    String URL =
        "jdbc:mysql://localhost/fogtest?serverTimezone=Europe/Copenhagen&allowPublicKeyRetrieval=true";
    String USER = "fogtest";
    String PASS = "codergram";

    InputStream stream = IntergrationTest.class.getClassLoader().getResourceAsStream("init.sql");
    if (stream == null) throw new RuntimeException("init.sql");
    try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
      conn.setAutoCommit(false);
      ScriptRunner runner = new ScriptRunner(conn);
      runner.setStopOnError(true);
      runner.setLogWriter(null);
      runner.runScript(new BufferedReader(new InputStreamReader(stream)));
      conn.commit();
    } catch (SQLException throwables) {
      log.error(throwables.getMessage());
    }
    log.info("Migration script done");
  }

  @BeforeAll
  static void setupAPI() {
    resetTestDatabase();

    String url = "jdbc:mysql://localhost:3306/fogtest?serverTimezone=CET";
    String user = "fogtest";
    String psw = "codergram";

    Database db = new Database(url, user, psw);
    db.runMigrations();

    DBUser dbUser = new DBUser(db);
    GoogleAuthService tfa = new GoogleAuthService();

    api = new Api(dbUser, tfa);

    // Create user
    try {
      api.createUser("test", "test@test.dk", "1234", Role.USER);
    } catch (UserNotFound userNotFound) {
      userNotFound.printStackTrace();
    }
  }

  @Test
  @DisplayName("Userstory 1")
  void us1() {
    String email = "test@test.dk";
    String password = "1234";

    // Login user
    log.info("Trying to login with correct credentials");
    try {
      user = api.login(email, password);
      assertEquals(email, user.getEmail());
    } catch (LoginError e) {
      log.warn(e.getMessage());
    }

    // Invalid login
    log.info("Trying to login with wrong credentials");
    try {
      User user1 = api.login(user.getEmail(), "2233");
      assertNull(user1.getEmail());
    } catch (LoginError e) {
      log.warn(e.getMessage());
    }
  }
}
