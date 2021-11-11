/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.admin;

import static org.slf4j.LoggerFactory.getLogger;

import api.Api;
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
    name = "SetupTOTP",
    urlPatterns = {"/SetupTOTP"})
public class SetupTOTP extends BaseServlet {

  private static final Logger log = getLogger(SetupTOTP.class);

  protected void render(HttpServletRequest request, HttpServletResponse response) {
    try {
      super.render("Setup TOTP", "/WEB-INF/pages/admin/setuptotp.jsp", request, response);
    } catch (ServletException | IOException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    try {
      User curUser = (User) req.getSession().getAttribute("user");

      log("Trying to log into admin :" + curUser);

      if (curUser == null || !curUser.isAdmin()) {
        log("User is not admin: " + curUser);
        resp.sendError(401);
      } else {
        String totpSecret = api.generateTOTPSecret();
        curUser.setTotp(totpSecret);
        req.getSession().setAttribute("user", curUser);

        req.setAttribute("qrCode", api.getQRCode(curUser));

        req.getSession().setMaxInactiveInterval(Api.MAX_ADMIN_SESSION_TIME * 60);
        render(req, resp);
      }

    } catch (Exception e) {
      log(e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp){
    try {

      User curUser = (User) req.getSession().getAttribute("user");

      String totpCode = req.getParameter("inputTOTP");

      if (api.checkTOTP(curUser.getTotp(), totpCode)) {
        log.debug("Secret: {}", curUser.getTotp());
        log.debug("Provided: {}", totpCode);
        req.getSession().setAttribute("user", curUser);
        api.saveTotp(curUser);
        resp.sendRedirect(req.getContextPath() + "/AdminPage");
      } else {
        throw new LoginError("Wrong auth code");
      }

      } catch (LoginError l) {
        req.setAttribute("errorMsg", l.getMessage());
        req.setAttribute("error", true);
        render(req, resp);
      } catch (Exception e){
      log(e.getMessage());
    }
  }
}
