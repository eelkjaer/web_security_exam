/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package domain.user;

import static org.junit.jupiter.api.Assertions.*;

import domain.user.User.Role;
import org.junit.jupiter.api.Test;

class UserTest {

  private User testUser = new User(-1, "TestUser", "test@mail.dk", Role.USER);

  @Test
  public void testValidPasswordValidaton(){
    String password = "aCegikm5p!rt";
    assertTrue(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordSameLetters(){
    String password = "aaaaikmprt";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordToShort(){
    String password = "ikmprt";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordBadSequence(){
    String password = "ikmprtabcd";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordNumberSequence(){
    String password = "ikmprtacfotnf56";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordSequenceSpecialCharacter(){
    String password = "ikmprtacfotnf57%&";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testValidPasswordSequenceSpecialCharacter(){
    String password = "ikmprtAcfotnf57%!";
    assertTrue(testUser.validatePassword(password));
  }

  @Test
  public void testValidPasswordSequenceSpace(){
    String password = "ikmp rtacf oTnf5 7%!";
    assertTrue(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordSequenceDanishCharater(){
    String password = "ikmprtaæøcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testValidPasswordSequenceDanishCharater(){
    String password = "ikmprTaæåcfotn!2";
    assertTrue(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordFirstCharacterBig(){
    String password = "Ikmprtaæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordFirstCharacterNumber(){
    String password = "5kmprtaæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordFirstCharacterSpecial(){
    String password = "!kmprtaæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordNoBigLetter(){
    String password = "akmpr!4taæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordNoNumber(){
    String password = "akmpr!Ttaæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordNoSpecialCharacter(){
    String password = "akmpr5Ttaæåcfotn";
    assertFalse(testUser.validatePassword(password));
  }

  @Test
  public void testInvalidPasswordNoSmallLetters(){
    String password = "KGTED4765%)€BSE";
    assertFalse(testUser.validatePassword(password));
  }
}