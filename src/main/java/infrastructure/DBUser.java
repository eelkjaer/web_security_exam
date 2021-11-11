/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure;

import static org.slf4j.LoggerFactory.getLogger;

import domain.user.User;
import domain.user.UserRepository;
import domain.user.exceptions.UserException;
import domain.user.exceptions.UserNotFound;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
      throw new UserNotFound(ex.getMessage());
    }
  }

  @Override
  public User createUser(User user) throws UserException {
    try (Connection conn = database.getConnection()) {

      // Prepare a SQL statement from the DB connection
      String query = "INSERT INTO users (name, email, role, password, totp) VALUES (?, ?, ?, ?, ?)";
      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        // Link variables to the SQL statement
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getRole().name());
        ps.setString(4, user.getPassword());
        ps.setString(5, "");

        // Execute the SQL statement to update the DB
        ps.executeUpdate();

        // Optional: Get result from the SQL execution, that returns the executed keys (user_id,
        // user_name etc..)
        rs = ps.getGeneratedKeys();

        // Search if there is a result from the DB execution
        if (rs.next()) {
          // Create user from the user_id key that is returned form the DB execution
          return user.withId(rs.getInt(1));

        } else {
          // Return null, if no result is returned form the execution
          return null;
        }
      }
    } catch (SQLException ex) {
      throw new UserException();
    }
  }

  @Override
  public void updateUserById(int userId, User user) throws UserNotFound {
    try (Connection conn = database.getConnection()) {

      // Prepare a SQL statement from the DB connection
      String query = "UPDATE users SET role = ?" + " WHERE id = ? ";
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setString(1, user.getRole().name());
        ps.setInt(2, userId);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      throw new UserNotFound();
    }
  }

  @Override
  public void deleteUserById(int userId) throws UserNotFound {
    try (Connection conn = database.getConnection()) {

      // Deactivate and don't delete data
      String query = "UPDATE users SET active = 0 WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setInt(1, userId);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      throw new UserNotFound();
    }
  }

  @Override
  public void changeUserRole(int userId, User.Role role) throws UserNotFound {
    try (Connection conn = database.getConnection()) {

      // Prepare a SQL statement from the DB connection
      String query = "UPDATE users SET role = ? " + " WHERE id = ? ";
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setString(1, role.name());
        ps.setInt(2, userId);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      throw new UserNotFound();
    }
  }

  @Override
  public void loggedIn(int userId) throws UserNotFound{
    try (Connection conn = database.getConnection()) {

      // Deactivate and don't delete data
      String query = "UPDATE users SET last_login = NOW() WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setInt(1, userId);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      throw new UserNotFound();
    }
  }

  @Override
  public void saveTOTP(int userId, String totpSecret) throws UserNotFound {
    try (Connection conn = database.getConnection()) {

      // Deactivate and don't delete data
      String query = "UPDATE users SET totp = ? WHERE id = ?";
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setString(1, totpSecret);
        ps.setInt(2, userId);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }

    } catch (SQLException ex) {
      throw new UserNotFound();
    }
  }

  @Override
  public User getUserByEmail(String email) throws UserNotFound {
    try (Connection conn = database.getConnection()) {

      // Prepare a SQL statement from the DB connection
      String query = "SELECT * FROM users WHERE email = ? AND active = 1";
      ResultSet rs;
      try (PreparedStatement ps = conn.prepareStatement(query)) {

        // Link variables to the SQL statement
        ps.setString(1, email);

        // Execute the SQL query and save the result
        rs = ps.executeQuery();

        // Search if there is a result from the DB execution
        if (rs.next()) {
          int userId = rs.getInt(USERS_ID);
          String usersName = rs.getString(USERS_NAME);
          String userMail = rs.getString(USERS_EMAIL);
          User.Role userRole = User.Role.valueOf(rs.getString(USERS_ROLE));
          String lastLogin = String.valueOf(rs.getTimestamp(USERS_LAST_LOGIN));
          String userPassword = rs.getString("users.password");
          String userTOTP = rs.getString("users.totp");

          return new User(userId, usersName, userMail, userRole, lastLogin, userPassword, userTOTP);

        } else {
          throw new UserNotFound();
        }
      }
    } catch (SQLException e) {
      throw new UserNotFound(e.getMessage());
    }
  }

  @Override
  public void saveToLog(int userId, String ip) {
    try (Connection conn = database.getConnection()) {
      // Prepare a SQL statement from the DB connection
      String query = "INSERT INTO login_log (user_id, success, ip, timestamp) VALUES (?, ?, ?, NOW())";
      try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        // Link variables to the SQL statement
        ps.setInt(1, userId);
        ps.setBoolean(2, userId != -1);
        ps.setString(3, ip);

        // Execute the SQL statement to update the DB
        ps.executeUpdate();
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
    }
  }
}
