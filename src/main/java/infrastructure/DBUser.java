/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure;

import static org.slf4j.LoggerFactory.getLogger;

import domain.user.Log;
import domain.user.User;
import domain.user.UserRepository;
import domain.user.exceptions.UserException;
import domain.user.exceptions.UserNotFound;
import domain.user.exceptions.UserSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class DBUser implements UserRepository {
  private final Database database;
  private static final Logger log = getLogger(DBUser.class);

  private static final String USERS_ID = "users.id";
  private static final String USERS_NAME = "users.name";
  private static final String USERS_EMAIL = "users.email";
  private static final String USERS_ROLE = "users.role";
  private static final String USERS_LAST_LOGIN = "users.last_login";

  public DBUser(Database database) {
    this.database = database;
  }

  @Override
  public List<User> getAllUsersFromDB() throws UserNotFound {
    List<User> allUsersFromDB = new ArrayList<>();

    try (Connection conn = database.getConnection()) {

      String query = "SELECT * FROM users WHERE active = 1";

      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        rs = ps.executeQuery();

        while (rs.next()) {
          int userId = rs.getInt(USERS_ID);
          String usersName = rs.getString(USERS_NAME);
          String userEmail = rs.getString(USERS_EMAIL);
          String userRole = rs.getString(USERS_ROLE);
          String lastLogin = String.valueOf(rs.getTimestamp(USERS_LAST_LOGIN));

          User user = new User(userId, usersName, userEmail, User.Role.valueOf(userRole), lastLogin);

          allUsersFromDB.add(user);
        }
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }

    return allUsersFromDB;
  }

  @Override
  public User getUserById(int userId) throws UserNotFound {
    User user = null;

    try (Connection conn = database.getConnection()) {

      String query = "SELECT * FROM users WHERE id = ?";

      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, userId);
        rs = ps.executeQuery();

        if (rs.next()) {
          String usersName = rs.getString(USERS_NAME);
          String userEmail = rs.getString(USERS_EMAIL);
          String userRole = rs.getString(USERS_ROLE);
          String lastLogin = String.valueOf(rs.getTimestamp(USERS_LAST_LOGIN));

          user = new User(userId, usersName, userEmail, User.Role.valueOf(userRole), lastLogin);

          return user;
        } else {
          throw new UserNotFound();
        }
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public User createUser(User user) throws UserException {
    try (Connection conn = database.getConnection()) {
      String query = "INSERT INTO users (name, email, role, password, totp) VALUES (?, ?, ?, ?, ?)";
      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getRole().name());
        ps.setString(4, user.getPassword());
        ps.setString(5, null);
        ps.executeUpdate();

        rs = ps.getGeneratedKeys();

        if (rs.next()) {
          return user.withId(rs.getInt(1));

        } else {
          return null;
        }
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserException();
    }
  }

  @Override
  public void updateUserById(int userId, User user) throws UserNotFound {
    try (Connection conn = database.getConnection()) {
      String query = "UPDATE users SET role = ?" + " WHERE id = ? ";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, user.getRole().name());
        ps.setInt(2, userId);
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public void deleteUserById(int userId) throws UserNotFound {
    try (Connection conn = database.getConnection()) {
      String query = "UPDATE users SET active = 0 WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, userId);
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public void changeUserRole(int userId, User.Role role) throws UserNotFound {
    try (Connection conn = database.getConnection()) {
      String query = "UPDATE users SET role = ? " + " WHERE id = ? ";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, role.name());
        ps.setInt(2, userId);
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public void loggedIn(int userId) throws UserNotFound{
    try (Connection conn = database.getConnection()) {
      String query = "UPDATE users SET last_login = NOW() WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, userId);
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public void saveTOTP(int userId, String totpSecret) throws UserNotFound {
    try (Connection conn = database.getConnection()) {
      String query = "UPDATE users SET totp = ? WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, totpSecret);
        ps.setInt(2, userId);
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      log.error(ex.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public User getUserByEmail(String email) throws UserNotFound, UserSecurityException {
    try (Connection conn = database.getConnection()) {
      String query = "SELECT * FROM users WHERE email = ?";
      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, email);
        rs = ps.executeQuery();

        if (rs.next()) {
          int userId = rs.getInt(USERS_ID);
          String usersName = rs.getString(USERS_NAME);
          String userMail = rs.getString(USERS_EMAIL);
          User.Role userRole = User.Role.valueOf(rs.getString(USERS_ROLE));
          String lastLogin = String.valueOf(rs.getTimestamp(USERS_LAST_LOGIN));
          String userPassword = rs.getString("users.password");
          String userTOTP = rs.getString("users.totp");

          if(rs.getInt("users.active") == 0) throw new UserSecurityException();

          return new User(userId, usersName, userMail, userRole, lastLogin, userPassword, userTOTP);

        } else {
          throw new UserNotFound();
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage());
      throw new UserNotFound();
    }
  }

  @Override
  public void saveToLog(int userId, String ip, boolean success) {
    try (Connection conn = database.getConnection()) {
      String query = "INSERT INTO login_log (user_id, success, ip, timestamp) VALUES (?, ?, ?, NOW())";
      try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, userId);
        ps.setBoolean(2, success);
        ps.setString(3, ip);

        ps.executeUpdate();
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
    }
  }

  @Override
  public List<Log> getLogs() {
    List<Log> logs = new ArrayList<>();
    try (Connection conn = database.getConnection()) {

      String query = "SELECT * FROM login_log";

      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        rs = ps.executeQuery();
        while (rs.next()) {
          int userId = rs.getInt("login_log.user_id");
          boolean success = rs.getBoolean("login_log.success");
          String ip = rs.getString("login_log.ip");
          Timestamp timestamp = rs.getTimestamp("login_log.timestamp");

          Log l = new Log(ip, userId, timestamp.toString(), success);
          logs.add(l);
        }
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
    }
    return logs;
  }
}
