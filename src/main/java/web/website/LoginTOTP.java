/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.website;

import static org.slf4j.LoggerFactory.getLogger;

import domain.user.User;
import domain.user.exceptions.LoginError;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import web.BaseServlet;

@WebServlet(
    name = "LoginTOTP",
    urlPatterns = {"/LoginTOTP"})
public class LoginTOTP extends BaseServlet {

  private static final Logger log = getLogger(LoginTOTP.class);

  protected void render(HttpServletRequest request, HttpServletResponse response) {
    try {
      super.render("Log ind", "/WEB-INF/pages/totp.jsp", request, response);
    } catch (ServletException | IOException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    render(req, resp);

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      // Make a check that 2FA is correct if user is admin
      if(request.getParameter("target").equals("totp")) {
        log.debug("Got 2FA active");
        User usr = (User) request.getSession().getAttribute("nuser");
        String providedCode = request.getParameter("inputTOTP");

        if (api.checkTOTP(usr.getTotp(), providedCode)) {
          log.debug("Secret: %s", usr.getTotp());
          log.debug("Provided: %s", providedCode);
          request.getSession().setAttribute("user", usr);
          request.getSession().removeAttribute("nuser");
          response.sendRedirect(request.getContextPath() + "/AdminPage");
        } else {
          throw new LoginError("Wrong auth code");
        }
      }

    } catch (LoginError i) {
      request.setAttribute("errorMsg", i.getMessage());
      request.setAttribute("error", true);
      render(request, response);
    }
  }
}
