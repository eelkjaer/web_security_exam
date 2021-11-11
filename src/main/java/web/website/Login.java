/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.website;

import static org.slf4j.LoggerFactory.getLogger;

import api.Api;
import api.exceptions.RecaptchaException;
import domain.user.User;
import domain.user.exceptions.LoginError;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import web.BaseServlet;

@WebServlet(
    name = "Login",
    urlPatterns = {"/Login"})
public class Login extends BaseServlet {

  private static final Logger log = getLogger(Login.class);

  protected void render(HttpServletRequest request, HttpServletResponse response) {
    try {
      super.render("Log ind", "/WEB-INF/pages/login.jsp", request, response);
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
    String ipAddr = request.getHeader("X-FORWARDED-FOR").split(",")[0];

    try {
      if(api.verifyRecaptcha(request.getParameter("g-recaptcha-response"))){
        throw new RecaptchaException();
      }

      request.setAttribute("providedMail", request.getParameter("inputEmail"));
      User curUser = login(request);


      if (curUser != null){
        api.saveToLog(curUser, ipAddr);

        if (curUser.isAdmin()) {
          log.info("User {} is admin", curUser.getEmail());
          if(curUser.isTOTP()){
            log.info("User {} is totp", curUser.getEmail());
            request.getSession().setAttribute("nuser", request.getSession().getAttribute("user"));
            request.getSession().removeAttribute("user");
            response.sendRedirect(request.getContextPath() + "/LoginTOTP");
          } else {
            log.info("User {} is not totp", curUser.getEmail());
            response.sendRedirect(request.getContextPath() + "/AdminPage");
          }
        } else {
          response.sendRedirect(request.getContextPath() + "/UserPage");
        }
      } else {
        response.sendRedirect(request.getContextPath() + "/");
      }

    } catch (LoginError i) {

      api.saveToLog(null, ipAddr);

      String errormsg = i.getMessage();

      request.setAttribute("errorMsg", errormsg);
      request.setAttribute("error", true);
      render(request, response);
    } catch (RecaptchaException e) {
      request.setAttribute("errorMsg", e.getMessage());
      request.setAttribute("error", true);
      render(request, response);
    }
  }

  private User login(HttpServletRequest req) throws LoginError {
    HttpSession session = req.getSession();

    String usrEmail = req.getParameter("inputEmail");
    String usrPassword = req.getParameter("inputPassword");
    User curUsr = null;

    curUsr = api.login(usrEmail, usrPassword);

    session.setAttribute("user", curUsr);
    session.setAttribute("userrole", curUsr.getRole().name());
    if(curUsr.isAdmin()) req.getSession().setMaxInactiveInterval(Api.MAX_ADMIN_SESSION_TIME * 60);

    return curUsr;
  }
}
