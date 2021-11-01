/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package infrastructure;

import api.Api;
import api.TwoFactorAuthService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

public class GoogleAuthService implements TwoFactorAuthService {

  @Override
  public String generateSecretKey() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[20];
    random.nextBytes(bytes);
    Base32 base32 = new Base32();
    return base32.encodeToString(bytes);
  }

  @Override
  public String getTOTPCode(String secretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
  }

  @Override
  public String getGABarCode(String secretKey, String account) {
    return "otpauth://totp/"
        + URLEncoder.encode(Api.GENERIC_SITE_TITLE + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
        + "?secret="
        + URLEncoder.encode(secretKey, StandardCharsets.UTF_8).replace("+", "%20")
        + "&issuer="
        + URLEncoder.encode(Api.GENERIC_SITE_TITLE, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static String encodeFileToBase64Binary(String fileName) throws IOException {
    File file = new File(fileName);
    byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
    return new String(encoded, StandardCharsets.US_ASCII);
  }

  @Override
  public String createQRCode(String barCodeData) throws WriterException, IOException {
    BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
        400, 400);

    String fileDir = System.getProperty("java.io.tmpdir");
    String uuid = UUID.randomUUID().toString().replace("-", "");
    String fileName = uuid + ".png";
    fileDir = fileDir + fileName;

    try (FileOutputStream fos = new FileOutputStream(fileDir)) {
        MatrixToImageWriter.writeToStream(matrix, "png", fos);
    }

    return encodeFileToBase64Binary(fileDir);
  }
}
