package it.polimi.tiw.project4.controllers;

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

@WebServlet("/signup")
public class SignUp extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public SignUp() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtain and escape params
        final String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        final String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
        final String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        final String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        final String confirmPassword = StringEscapeUtils.escapeJava(request.getParameter("confirmPassword"));
        final String redirectPath = "../index.html";

        if (name == null || surname == null || email == null || password == null || confirmPassword == null ||
                name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid registration credentials");
            return;
        }

        // We don't bother checking for full email addresses validity, this is just a basic check
        if (!email.contains("@")) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("nameSignup", name);
            ctx.setVariable("surnameSignup", surname);
            ctx.setVariable("msgSignup", "Invalid email address");
            templateEngine.process(redirectPath, ctx, response.getWriter());
            return;
        }
        if (!password.equals(confirmPassword)) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("emailSignup", email);
            ctx.setVariable("nameSignup", name);
            ctx.setVariable("surnameSignup", surname);
            ctx.setVariable("msgSignup", "Passwords did not match");
            templateEngine.process(redirectPath, ctx, response.getWriter());
            return;
        }

        // Query DB to check if the user already exists
        UserDAO userDao = new UserDAO(connection);
        try {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            // If the user exists, show the signup page with an error message
            // Otherwise, create a user and an associated account and return to the login page
            if (!userDao.userExists(email)) {
                userDao.createUser(name, surname, email, password);
                ctx.setVariable("msgSignup", "User created successfully");
                templateEngine.process(redirectPath, ctx, response.getWriter());
            } else {
                ctx.setVariable("msgSignup", "A user already exists with this email address");
                templateEngine.process(redirectPath, ctx, response.getWriter());
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create a new user");
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