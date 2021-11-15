/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

package web.website;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import web.BaseServlet;

@WebServlet(
    name = "Index",
    urlPatterns = {"", "/"})
public class Index extends BaseServlet {

  private static final Logger log = getLogger(Index.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (req.getSession().getAttribute("user") != null){
      log.info("No user in session");
      redirect(req, resp, "UserPage");
      return;
    }

    log.info("Serving page {}", req.getRequestURI());

    try {
      render("Startside", "/WEB-INF/pages/index.jsp", req, resp);
    } catch (ServletException | IOException e) {
      log.error(e.getMessage());
    }
  }
}
