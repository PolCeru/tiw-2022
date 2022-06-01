package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.UserDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.TemplateEngineHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/login")
public class Login extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public Login() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Obtain and escape params
        String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid credentials");
            return;
        }

        // Query DB to authenticate the user
        UserDAO userDao = new UserDAO(connection);
        User user;
        try {
            user = userDao.getUser(email, password);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to check credentials");
            return;
        }

        // If the user exists, add info to the session and go to home page, otherwise
        // show login page with error message
        if (user == null) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("email", email);
            ctx.setVariable("errorMsg", "Incorrect username or password");
            templateEngine.process("/index.html", ctx, response.getWriter());
        } else {
            request.getSession().setAttribute("userid", user.getId());
            String path = getServletContext().getContextPath() + "/home";
            response.sendRedirect(path);
        }
    }

    @Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}