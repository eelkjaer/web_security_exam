/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user;

import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import domain.user.exceptions.UserSecurityException;
import java.util.List;

public interface UserRepository extends UserFactory {

  List<User> getAllUsersFromDB() throws LoginError, UserNotFound;

  User getUserById(int userId) throws UserNotFound;

  User getUserByEmail(String email) throws UserNotFound, UserSecurityException;

  void updateUserById(int userId, User user) throws UserNotFound;

  void deleteUserById(int userId) throws UserNotFound;

  void changeUserRole(int userId, User.Role role) throws UserNotFound;

  void loggedIn(int userId) throws UserNotFound;

  void saveTOTP(int userId, String totpSecret) throws UserNotFound;

  void saveToLog(int userId, String ip);
}
