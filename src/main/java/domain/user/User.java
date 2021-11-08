/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user;

import api.Api;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class User implements Serializable {

  private final String name;
  private final String email;
  private final Enum<Role> role;
  private final String password;
  private String totp;
  private int id;

  public User(int id, String name, String email, Enum<Role> role, String password, String totp) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.password = password;
    this.totp = totp;
  }

  public User(int id, String name, String email, Enum<Role> role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    password = null;
    totp = null;
  }

  public boolean isTOTP(){
    return !this.totp.isEmpty() || !this.totp.isBlank();
  }

  public static Enum<Role> valueOfIgnoreCase(String search) {
    for (Enum<Role> e : Role.values()) {
      if (e.name().equalsIgnoreCase(search)) {
        return e;
      }
    }
    return null;
  }


  public static String calculateSecret(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt() + Api.PASSWORD_PEPPER);
  }

  public boolean isPasswordCorrect(String password) {
    return BCrypt.checkpw(password, this.password);
  }

  /**
   * Requirements:
   * Min 8 characters, max 20 characters.
   * Min 1 digit
   * Min 1 upper case letter
   * Min 1 lower case alphabet
   * Min 1 special character which includes !@#$%&*()-+=^.
   * No whitespaces
   * @param password Password to check
   * @return true if requirements are met, otherwise false
   */
  public static boolean checkPasswordStrength(String password) {
    if (password == null) return false;

    String regex = "^(?=.*[0-9])"
        + "(?=.*[a-z])(?=.*[A-Z])"
        + "(?=.*[@#$%^&+=])"
        + "(?=\\S+$).{8,20}$";

    /*
    RegEXplainer:

    ^ represents starting character of the string.
    (?=.*[0-9]) represents a digit must occur at least once.
    (?=.*[a-z]) represents a lower case alphabet must occur at least once.
    (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
    (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
    (?=\\S+$) white spaces don’t allowed in the entire string.
    .{8, 20} represents at least 8 characters and at most 20 characters.
    $ represents the end of the string.
    */

    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(password);

    return m.matches();
  }

  public static boolean validatePassword(String password){
    boolean condition1 = false;
    boolean condition2 = false;
    boolean condition3 = false;
    boolean condition4 = false;

    condition1 = checkPassswordLenth(password);

    condition2 = checkSequential(password);

    condition3 = checkTheFirstCharacterInPassword(password);

    condition4 = checkPasswordComposition(password);

    return condition1 && condition2 && condition3 && condition4;
  }

  private static boolean checkPassswordLenth(String password){

    return password.length() >= 8 && password.length() <= 128;
  }

  private static boolean checkSequential(String password){
    boolean isValid = false;
    if(password.length() > 1){
      isValid = true;

      for(int i = 1 ; i < password.length() ; i ++){
        char char1 = password.charAt(i - 1);
        char char2 = password.charAt(i);
        String value1 = String.valueOf(char1);
        String value2 = String.valueOf(char2);
        int asciivalue1 = password.charAt(i - 1);
        int asciivalue2 = password.charAt(i);

        // Sequential 1,2,3
        if(Character.isDigit(char1) && Character.isDigit(char2)){
          if(Integer.parseInt(value1) + 1 == Integer.parseInt(value2)){
            isValid = false;
          }
        }
        // Sequential a,a,a
        else if (value1.equals(value2)){
          isValid = false;
        }
        // Sequential a,b,c
        else if (asciivalue1 + 1 == asciivalue2){
          isValid = false;
        }
        // æ,ø,å
        else if (value1.equalsIgnoreCase("æ") && value2.equalsIgnoreCase("ø")){
          isValid = false;
        }
        else if (value1.equalsIgnoreCase("ø") && value2.equalsIgnoreCase("å")){
          isValid = false;
        }
      }
      return isValid;
    }
    return isValid;
  }

  private static boolean checkTheFirstCharacterInPassword(String password){
    boolean isValid = false;
    if(password.length() > 1) {
      isValid = true;
      char firstChar = password.charAt(0);
      int asciivalue = firstChar;
      System.out.println(firstChar);
      System.out.println(asciivalue);

      // Check for big letters
      if(asciivalue >= 65 && asciivalue <= 90){
        isValid = false;
      }
      // Check for numbers
      else if(asciivalue >= 48 && asciivalue <= 57){
        isValid = false;
      }
      // Check for special characters
      else if((asciivalue >= 33 && asciivalue <= 47) || (asciivalue >= 58 && asciivalue <= 96) || (asciivalue >= 123 && asciivalue <= 126)){
        isValid = false;
      }
    }
    return isValid;
  }

  private static boolean checkPasswordComposition(String password){
    boolean hasUppercaseLetter = false;
    boolean hasLowercaseLetter = false;
    boolean hasNumber = false;
    boolean hasSpecialCharacter = false;

    for(int i = 1 ; i < password.length() ; i ++){
      char currentChar = password.charAt(i);
      int asciivalue = currentChar;

      // Uppercase letter
      if((asciivalue >= 65 && asciivalue <= 90) || (currentChar == 'Æ' || currentChar == 'Ø' || currentChar == 'Å')){
        hasUppercaseLetter = true;
      }
      // Lowercase letter
      else if((asciivalue >= 97 && asciivalue <= 122) || (currentChar == 'æ' || currentChar == 'ø' || currentChar == 'å')){
        hasLowercaseLetter = true;
      }
      // Number
      else if(asciivalue >= 48 && asciivalue <= 57){
        hasNumber = true;
      }
      // Special character
      else if((asciivalue >= 33 && asciivalue <= 47) || (asciivalue >= 58 && asciivalue <= 96) || (asciivalue >= 123 && asciivalue <= 126)){
        hasSpecialCharacter = true;
      }
    }

    return hasUppercaseLetter && hasLowercaseLetter && hasNumber && hasSpecialCharacter;
  }

  public boolean isAdmin() {
    return this.role == Role.ADMIN;
  }

  public int getId() {
    return id;
  }

  public User getById(int id) {
    return this.id != id ? this : null;
  }

  public User withId(int id) {
    this.id = id;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Enum<Role> getRole() {
    return role;
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

  public String getTotp() {
    return totp;
  }

  public void setTotp(String totp) {
    this.totp = totp;
  }

  @Override
  public String toString() {
    return "User{" +
        "name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", role=" + role +
        ", password=" + password +
        ", totp=" + totp +
        ", id=" + id +
        '}';
  }

  public enum Role {
    USER,
    ADMIN
  }
}
