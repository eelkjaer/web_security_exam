/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.admin;

import static org.slf4j.LoggerFactory.getLogger;

import domain.user.User;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import web.BaseServlet;

@WebServlet(
    name = "InputTOTP",
    urlPatterns = {"/InputTOTP"})
public class InputTOTP extends BaseServlet {

  private static final Logger log = getLogger(InputTOTP.class);

  protected void render(HttpServletRequest request, HttpServletResponse response) {
    try {
      super.render("Log ind", "/WEB-INF/pages/admin/totp.jsp", request, response);
    } catch (ServletException | IOException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    if (req.getSession().getAttribute("user") != null){
      redirect(req, resp, "AdminPage");
      return;
    }
    render(req, resp);

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      // Make a check that 2FA is correct if user is admin
      if(request.getParameter("target").equals("totp")) {
        log.debug("Got 2FA active");
        User usr = (User) request.getSession().getAttribute("user");
        String providedCode = request.getParameter("inputTOTP");

        if (api.checkTOTP(usr.getTotp(), providedCode)) {
          log.debug("Secret: {}", usr.getTotp());
          log.debug("Provided: {}", providedCode);
          request.getSession().setAttribute("user", usr);
          deleteUser(request);
          response.sendRedirect(request.getContextPath() + "/AdminPage");
        } else {
          throw new LoginError("Wrong auth code");
        }
      }

    } catch (LoginError i) {
      log.error(i.getMessage());
      request.setAttribute("errorMsg", i.getMessage());
      request.setAttribute("error", true);
      render(request, response);
    }
  }

  private void deleteUser(HttpServletRequest req) {
    try {
      int userId = (int) req.getAttribute("userToDel");
      api.deleteUser(userId);
    } catch (UserNotFound e) {
      log.error(e.getMessage());
    }
  }
}
