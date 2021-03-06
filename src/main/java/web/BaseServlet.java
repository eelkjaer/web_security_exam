/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web;

import static org.slf4j.LoggerFactory.getLogger;

import api.Api;
import infrastructure.DBUser;
import infrastructure.Database;
import infrastructure.GoogleAuthService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import web.widget.Navbar;

public class BaseServlet extends HttpServlet {

  protected static final Api api;
  private static final Logger log = getLogger(BaseServlet.class);
  private static final String ENCODING = "UTF-8";

  static {
    api = createApi();
  }

  private static Api createApi() {

    Database database = new Database();

    return new Api(
        new DBUser(database),
        new GoogleAuthService());
  }

  protected void render(
      String title, String content, HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setCharacterEncoding(ENCODING);
    response.setCharacterEncoding(ENCODING);

    addSecurityHeaders(response);

    request.setAttribute("title", Api.GENERIC_SITE_TITLE + " - " + title);
    request.setAttribute("content", content);
    request.setAttribute("navbar", new Navbar(request));
    request.setAttribute("recaptchaKey", Api.RECAPTCHA_SITEKEY);

    String ipAddr = api.getUserIp(request);
    log.info("{} requesting {}({})", ipAddr, title, content);
    //log.info("HTTP headers: {}", getAllHeaders(request));

    request.getRequestDispatcher("/WEB-INF/includes/base.jsp").forward(request, response);
  }

  private void addSecurityHeaders(HttpServletResponse response){
    response.addHeader("X-Frame-Options", "deny");
    response.addHeader("X-XSS-Protection", "1");
    response.addHeader("X-Content-Type-Options", "nosniff");
  }

  protected void redirect(HttpServletRequest req, HttpServletResponse resp, String servletName) {
    try {
      req.setCharacterEncoding(ENCODING);
      resp.setCharacterEncoding(ENCODING);
      resp.sendRedirect(req.getContextPath() + "/" + servletName);
    } catch (IOException ee) {
      log.error(ee.getMessage());
    }
  }

  private Map<String, List<String>> getAllHeaders(HttpServletRequest request){
    return Collections.list(request.getHeaderNames())
        .stream()
        .collect(Collectors.toMap(
            Function.identity(),
            h -> Collections.list(request.getHeaders(h))
        ));
  }
}
