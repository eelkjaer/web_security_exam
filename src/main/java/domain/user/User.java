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

  private int id;
  private final String name;
  private final String email;
  private final Enum<Role> role;
  private final String password;
  private String totp;
  private final String lastLogin;

  public User(int id, String name, String email, Enum<Role> role, String lastLogin, String password, String totp) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.password = password;
    this.totp = totp;
    this.lastLogin = lastLogin;
  }

  public User(int id, String name, String email, Enum<Role> role, String lastLogin) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    password = null;
    totp = null;
    this.lastLogin = lastLogin;
  }

  public boolean isTOTP(){
    return !this.totp.isEmpty() || !this.totp.isBlank() || this.totp != null;
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
      int asciiValue = password.charAt(0);

      // Check for big letters
      if(asciiValue >= 65 && asciiValue <= 90){
        return false;
      }
      // Check for numbers
      else if(asciiValue >= 48 && asciiValue <= 57){
        return false;
      }
      // Check for special characters
      else if((asciiValue >= 33 && asciiValue <= 47) || (asciiValue >= 58 && asciiValue <= 96) || (asciiValue >= 123 && asciiValue <= 126)){
        return false;
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

      // Uppercase letter
      if(((int) currentChar >= 65 && (int) currentChar <= 90) || (currentChar == 'Æ' || currentChar == 'Ø' || currentChar == 'Å')){
        hasUppercaseLetter = true;
      }
      // Lowercase letter
      else if(((int) currentChar >= 97 && (int) currentChar <= 122) || (currentChar == 'æ' || currentChar == 'ø' || currentChar == 'å')){
        hasLowercaseLetter = true;
      }
      // Number
      else if((int) currentChar >= 48 && (int) currentChar <= 57){
        hasNumber = true;
      }
      // Special character
      else if(((int) currentChar >= 33 && (int) currentChar <= 47) || (
          (int) currentChar >= 58 && (int) currentChar <= 96) || (
          (int) currentChar >= 123 && (int) currentChar <= 126)){
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

  public String getLastLogin() {
    return lastLogin;
  }

  public String getLastLoginDate(){
    return this.lastLogin != null ? this.lastLogin : "Never";
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", role=" + role +
        ", totp='" + totp + '\'' +
        ", lastLogin='" + lastLogin + '\'' +
        '}';
  }

  public enum Role {
    USER,
    ADMIN
  }
}
