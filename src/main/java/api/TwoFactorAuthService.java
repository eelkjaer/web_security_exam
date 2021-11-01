/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package api;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface TwoFactorAuthService {

  String generateSecretKey();

  String getTOTPCode(String secretKey);

  String getGABarCode(String secretKey, String account);

  String createQRCode(String barCodeData) throws WriterException, IOException;

}
