/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.website;

import static org.slf4j.LoggerFactory.getLogger;

import domain.user.User;
import domain.user.User.Role;
import domain.user.exceptions.LoginError;
import domain.user.exceptions.UserNotFound;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import web.BaseServlet;
import api.exceptions.RecaptchaException;

@WebServlet(
    name = "Register",
    urlPatterns = {"/Register"})
public class Register extends BaseServlet {

  private static final Logger log = getLogger(Register.class);

  protected void render(HttpServletRequest request, HttpServletResponse response) {
    try {
      super.render("Opret bruger", "/WEB-INF/pages/register.jsp", request, response);
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

    HttpSession session = request.getSession();

    if (session.getAttribute("user") != null){
        response.sendRedirect(request.getContextPath() + "/UserPage");
    }

    try {
      if(api.verifyRecaptcha(request.getParameter("g-recaptcha-response"))){
        throw new RecaptchaException();
      }


      String name = request.getParameter("inputName");
      String email = request.getParameter("inputEmail");
      String password = request.getParameter("inputPassword");

      if (!User.validatePassword(password)){
        throw new LoginError("Your password is not secure enough");
      }

      User newUser = api.createUser(name, email, password, Role.USER);
      session.setAttribute("user", newUser);
      session.setAttribute("userrole", newUser.getRole().name());

      response.sendRedirect(request.getContextPath() + "/UserPage");

    } catch (UserNotFound | RecaptchaException | LoginError i) {
      log.error(i.toString());
      request.setAttribute("errorMsg", i.getMessage());
      request.setAttribute("error", true);
      render(request, response);
    }
  }
}
