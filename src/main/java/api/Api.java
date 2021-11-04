/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package api;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.zxing.WriterException;
import domain.user.User;
import domain.user.UserRepository;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class Api {

  private static final String DEPLOYED = System.getenv("DEPLOYED");
  private static final String DEPLOYED_DOMAIN = System.getenv("DOMAIN");
  public static final String PASSWORD_PEPPER = System.getenv("PEPPER");
  public static final int MAX_ADMIN_SESSION_TIME = 5; //5 minutter
  public static final String GENERIC_SITE_TITLE = "HaxB00k";
  public static final String RECAPTCHA_SITEKEY = DEPLOYED != null ? System.getenv("RECAPTCHA_SITEKEY") : "6Lc1gLYcAAAAACVdy0pgs-cY-lATaydHiyOViS2z";
  private static final String RECAPTCHA_SECRET = DEPLOYED != null ? System.getenv("RECAPTCHA_SECRET") : "6Lc1gLYcAAAAAD0d7E5MLfaPXB7GzyqROkqkRMaH";
  public static final String DOMAIN = DEPLOYED != null ? DEPLOYED_DOMAIN : "localhost:8080";
  public static final int MAX_LOGIN_ATTEMPTS = 5;

  private static final Logger log = getLogger(Api.class);

  private final UserRepository userRepository;
  private final TwoFactorAuthService tfaService;

  public Api(
      UserRepository userRepository,
      TwoFactorAuthService tfaService) {
    this.userRepository = userRepository;
    this.tfaService = tfaService;
  }


  public boolean verifyRecaptcha(String gRecaptchaResponse) {
    return !Utils.verifyRecaptcha(gRecaptchaResponse, RECAPTCHA_SECRET);
  }

  public String generateTOTPSecret(){
    return tfaService.generateSecretKey();
  }

  public boolean checkTOTP(String secret, String providedCode){
    return tfaService.getTOTPCode(secret).equals(providedCode);
  }

  public String getQRCode(User currentUser){
    String toReturn = "";
    try{
      toReturn = tfaService.createQRCode(
          tfaService.getGABarCode(currentUser.getTotp(), currentUser.getEmail())
      );

    } catch (IOException | WriterException e) {
      e.printStackTrace();
    }

    return toReturn;
  }

  /**
   * @param name Users name
   * @param email Users email
   * @param password Users password (plain-text)
   * @param role Users role
   * @see User.Role
   * @return Complete User object with ID and UUID set.
   * @throws UserNotFound If user already exists in database
   */
  public User createUser(String name, String email, String password, User.Role role)
      throws UserNotFound {
    log.info("Trying to create user: \nUser Type: {}\nName: {}\nE-mail: {}\nPassword: {}", role, name, email, password);

    User user = new User(-1, name, email, role, User.calculateSecret(password), null);
    user = userRepository.createUser(user);

    log.info("User created with ID {}", user.getId());

    return user;
  }

  /**
   * @param id ID of user in database
   * @throws UserNotFound If user is not found in database
   */
  public void deleteUser(int id) throws UserNotFound {
    log.info("Trying to delete user with ID {}", id);
    userRepository.deleteUserById(id);
    log.info("User deleted with ID {}", id);
  }

  /**
   * @param email Email of user
   * @param password Password of user
   * @return User object if correct, otherwise nulled user object
   * @throws LoginError If password does not match
   */
  public User login(String email, String password) throws LoginError {
    User user = null;
    try {
      user = userRepository.getUserByEmail(email);
      log.debug("User trying to login: {}", user);
    } catch (UserNotFound userNotFound) {
      log.warn(userNotFound.getMessage());
    }
    if (user == null || !user.isPasswordCorrect(password)) {
      throw new LoginError();
    } else {
      // Return user if password is validated
      log.info("User logged in: {}", user);
      try {
        userRepository.loggedIn(user.getId());
      } catch (UserNotFound e) {
        log.warn(e.getMessage());
      }
      return user;
    }
  }

  /** @return A list of all users in database */
  public List<User> getUsers() {
    try {
      return userRepository.getAllUsersFromDB();
    } catch (UserNotFound | LoginError e) {
      log.warn(e.getMessage());
    }
    return new ArrayList<>();
  }

  public void saveTotp(User curUser) {
    try{
      userRepository.saveTOTP(curUser.getId(), curUser.getTotp());
    } catch (UserNotFound e){
      log.warn(e.getMessage());
    }
  }

  public void saveToLog(User curUser, String ip_addr){
    if(curUser == null){
      userRepository.saveToLog(-1, ip_addr);
    } else {
      userRepository.saveToLog(curUser.getId(), ip_addr);
    }
  }
}
