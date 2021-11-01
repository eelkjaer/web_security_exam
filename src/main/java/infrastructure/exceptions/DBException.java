/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure.exceptions;

public class DBException extends Exception {
  public DBException() {
    super("DB Error");
  }

  public DBException(String message) {
    super(message);
  }

  public DBException(String message, Throwable cause) {
    super(message, cause);
  }

  public DBException(Throwable cause) {
    super(cause);
  }

  public DBException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
