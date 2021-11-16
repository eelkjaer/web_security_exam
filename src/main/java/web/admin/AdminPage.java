/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.admin;

import static org.slf4j.LoggerFactory.getLogger;

import api.Api;
import domain.user.Log;
import domain.user.User;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import web.BaseServlet;

@WebServlet(
    name = "AdminPage",
    urlPatterns = {"/AdminPage"})
public class AdminPage extends BaseServlet {
  private static final Logger log = getLogger(AdminPage.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    try {
      if (req.getSession().getAttribute("user") == null){
        log.warn("No user in session");
        redirect(req, resp, "Login");
        return;
      }

      User curUser = (User) req.getSession().getAttribute("user");

      if(!curUser.isTOTP()) {
        log.info("Admin user dont have TOTP setup: {}", curUser.getId());
        redirect(req, resp,"SetupTOTP");
        return;
      }

      log.info("Trying to log into admin : {}", curUser.getId());

      if (!curUser.isAdmin()) {
        log.info("User is not admin: {}", curUser.getId());
        resp.sendError(401);
      } else {
        req.getSession().setMaxInactiveInterval(Api.MAX_ADMIN_SESSION_TIME * 60);
        List<User> users = List.copyOf(api.getUsers());
        List<Log> logs = List.copyOf(api.getAllLoginAttempts());
        req.setAttribute("users", users);
        req.setAttribute("loginLog", logs);
        log.info("User {} is admin", curUser.getId());
        render("Users", "/WEB-INF/pages/admin/admin.jsp", req, resp);
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response){
    try {
      String action = request.getParameter("action");
      String target = request.getParameter("target");
      if("totp".equals(target) && "deleteUser".equals(action)) {
        User usr = (User) request.getSession().getAttribute("user");
        String providedCode = request.getParameter("inputTOTP");

        if (api.checkTOTP(usr.getTotp(), providedCode)) {
          deleteUser(request);
          response.sendRedirect(request.getContextPath() + "/AdminPage");
        } else {
          throw new LoginError("Wrong auth code. Please try again");
        }
      }
      redirect(request, response, this.getServletName());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void deleteUser(HttpServletRequest req) {
    try {
      api.deleteUser(Integer.parseInt(req.getParameter("userid")));
    } catch (UserNotFound e) {
      log.error(e.getMessage());
    }
  }
}
