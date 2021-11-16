/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package api;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.zxing.WriterException;
import domain.user.Log;
import domain.user.User;
import domain.user.UserRepository;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserException;
import domain.user.exceptions.UserNotFound;
import domain.user.exceptions.UserSecurityException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

public class Api {

  private static final String DEPLOYED = System.getenv("DEPLOYED");
  private static final String DEPLOYED_DOMAIN = System.getenv("DOMAIN");
  public static final String PASSWORD_PEPPER = System.getenv("PEPPER");
  public static final int MAX_ADMIN_SESSION_TIME = 5; //minutes
  public static final String GENERIC_SITE_TITLE = "HaxB00k";
  public static final String RECAPTCHA_SITEKEY = DEPLOYED != null ? System.getenv("RECAPTCHA_SITEKEY") : "6Lc1gLYcAAAAACVdy0pgs-cY-lATaydHiyOViS2z";
  protected static final String RECAPTCHA_SECRET = DEPLOYED != null ? System.getenv("RECAPTCHA_SECRET") : "6Lc1gLYcAAAAAD0d7E5MLfaPXB7GzyqROkqkRMaH";
  public static final String DOMAIN = DEPLOYED != null ? DEPLOYED_DOMAIN : "localhost:8080";

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
    log.info("Verifying recatpcha response: {}", gRecaptchaResponse);
    return !Utils.verifyRecaptcha(gRecaptchaResponse);
  }

  public String generateTOTPSecret(){
    log.info("Generating TOTP secret");
    return tfaService.generateSecretKey();
  }

  public boolean checkTOTP(String secret, String providedCode){
    log.info("Comparing TOTP secrets: {} with {}", secret, providedCode);
    return tfaService.getTOTPCode(secret).equals(providedCode);
  }

  public String getQRCode(User currentUser, String totpSecret){
    log.info("Generating TOTP QR code for user {}", currentUser.getId());
    String toReturn = "";
    try{
      toReturn = tfaService.createQRCode(
          tfaService.getGABarCode(totpSecret, currentUser.getEmail())
      );
    } catch (IOException | WriterException e) {
      log.error("Exception generating TOTP QR: {}", e.getMessage());
    }
    return toReturn;
  }

  public User createUser(String name, String email, String password, User.Role role)
      throws UserException {
    log.info("Trying to create user: \nUser Type: {}\nName: {}\nE-mail: {}", role, name, email);

    User user = new User(-1, name, email, role, null, User.calculateSecret(password), null);
    user = userRepository.createUser(user);

    log.info("User created with ID {}", user.getId());

    return user;
  }

  public void deleteUser(int id) throws UserNotFound {
    log.info("Trying to delete user with ID {}", id);
    userRepository.deleteUserById(id);
    log.info("User deleted with ID {}", id);
  }

  public User login(String email, String password) throws LoginError {
    User user = null;
    try {
      user = userRepository.getUserByEmail(email);
      log.debug("User trying to login: {}", user);
    } catch (UserNotFound userNotFound) {
      log.warn(userNotFound.getMessage());
    } catch (UserSecurityException e) {
      throw new LoginError(e.getMessage());
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

  public List<User> getUsers() {
    try {
      log.info("Getting all users");
      return userRepository.getAllUsersFromDB();
    } catch (UserNotFound | LoginError e) {
      log.warn(e.getMessage());
    }
    return new ArrayList<>();
  }

  public void saveTotp(User curUser) {
    try{
      log.info("Saving TOTP secret for user {}", curUser.getId());
      userRepository.saveTOTP(curUser.getId(), curUser.getTotp());
    } catch (UserNotFound e){
      log.warn(e.getMessage());
    }
  }

  public void saveToLog(User curUser, String ipAddr){
    if(curUser == null){
      log.warn("Unknown trying to access from {}", ipAddr);
      userRepository.saveToLog(-1, ipAddr);
    } else {
      log.info("User {} trying to access from {}", curUser.getId(), ipAddr);
      userRepository.saveToLog(curUser.getId(), ipAddr);
    }
  }

  public String getUserIp(HttpServletRequest request) {
    String ipAddr = "";
    if(request.getHeader("X-FORWARDED-FOR") != null){
      ipAddr = request.getHeader("X-FORWARDED-FOR").split(",")[0];
    } else {
      ipAddr = request.getRemoteAddr();
    }

    return ipAddr;
  }

  public List<Log> getAllLoginAttempts(){
    log.info("Getting all login attempts");
    return userRepository.getLogs();
  }
}
