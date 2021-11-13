/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user.exceptions;

public class UserSecurityException extends Exception {
  public UserSecurityException(String message) {
    super(message);
  }

  public UserSecurityException() {
    super("Your account have been disabled for security reasons.");
  }
}
