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
import domain.user.User.Role;
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

  /**
   * Renders the index.jsp page
   *
   * @see BaseServlet
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    try {
      User curUser = (User) req.getSession().getAttribute("user");

      if(!curUser.isTOTP()) resp.sendRedirect(req.getContextPath() + "/SetupTOTP");

      req.setAttribute("qrCode", api.getQRCode(curUser));

      log("Trying to log into admin :" + curUser);

      if (!curUser.isAdmin()) {
        log("User is not admin: " + curUser);
        resp.sendError(401);
      } else {
        req.getSession().setMaxInactiveInterval(Api.MAX_ADMIN_SESSION_TIME * 60);
        List<User> users = List.copyOf(api.getUsers());
        req.setAttribute("userlist", users);
        log("User is admin: " + curUser);
        render("Users", "/WEB-INF/pages/admin/admin.jsp", req, resp);
      }

    } catch (Exception e) {
      log(e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp){
    try {
      String action = req.getParameter("action");
      if ("deleteUser".equals(action)) {
        deleteUser(req);
      } else if ("createUser".equals(action)) {
        String name = req.getParameter("inputName");
        String mail = req.getParameter("inputEmail");
        String password = req.getParameter("inputPsw");
        Role role = Role.valueOf(req.getParameter("inputRole"));
        api.createUser(name, mail, password, role);
      }
      redirect(req, resp, this.getServletName());
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
