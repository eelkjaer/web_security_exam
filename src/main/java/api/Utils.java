/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package api;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class Utils {

  private static final Logger log = getLogger(Utils.class);

  private Utils() {}

  /** @return File content as String */
  public static String fileToString(String file) {
    if (file == null || file.isEmpty()) return null;
    try (InputStream input = Api.class.getClassLoader().getResourceAsStream(file)) {
      if (input == null) return null;
      return IOUtils.toString(input);
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
    return null;
  }

  protected static boolean verifyRecaptcha(String gRecaptchaResponse){
    String url = "https://www.google.com/recaptcha/api/siteverify";

    if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
      return false;
    }

    try{
      URL obj = new URL(url);
      HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

      String postParams = "secret=" + Api.RECAPTCHA_SECRET + "&response="
          + gRecaptchaResponse;

      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(postParams);
      wr.flush();
      wr.close();

      log.debug("Sending 'POST' request to URL: {}", url);
      log.debug("Post parameters: {}", postParams);
      log.debug("Response Code: {}", con.getResponseCode());

      BufferedReader in = new BufferedReader(new InputStreamReader(
          con.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      // print result
      log.debug("Response: {}", response);

      //parse JSON response and return 'success' value
      JsonObject jsonObject;
      try (JsonReader jsonReader = Json.createReader(new StringReader(response.toString()))) {
        jsonObject = jsonReader.readObject();
      }

      return jsonObject.getBoolean("success");
    }catch(Exception e){
      log.error(e.getMessage());
      return false;
    }

  }
}
