/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package api.exceptions;

public class RecaptchaException extends Exception {

  public RecaptchaException() {
    super("You missed the Captcha");
  }
}
