/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user.exceptions;

public class UserException extends Exception {
  public UserException(String message) {
    super(message);
  }

  public UserException() {
    super("Could not create user");
  }
}
