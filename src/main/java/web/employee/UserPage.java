/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.employee;


import domain.user.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import web.BaseServlet;

@WebServlet(
    name = "UserPage",
    urlPatterns = {"/UserPage"})
public class UserPage extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      User curUser = (User) req.getSession().getAttribute("user");

      log("Trying to log into admin :" + curUser);

      if (curUser == null) {
        log("User is not logged in");
        resp.sendError(401);
      } else {
        req.setAttribute("currentUser", curUser);

        log("User is logged in: " + curUser);
        render("UserPage", "/WEB-INF/pages/user/user.jsp", req, resp);
      }

    } catch (Exception e) {
      log(e.getMessage());
    }
  }
}
