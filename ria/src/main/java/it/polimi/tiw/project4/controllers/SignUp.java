package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.UserDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import org.apache.commons.text.StringEscapeUtils;

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
public class SignUp extends HttpServlet implements JsonServlet {
    private Connection connection = null;

    public SignUp() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtain and escape params
        final String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        final String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
        final String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        final String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        final String confirmPassword = StringEscapeUtils.escapeJava(request.getParameter("confirmPassword"));

        if (name == null || surname == null || email == null || password == null || confirmPassword == null ||
                name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Please provide valid credentials");
            return;
        }

        // We don't bother checking for full email addresses validity, this is just a basic check
        if (!email.contains("@")) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid email address");
            return;
        }
        if (!password.equals(confirmPassword)) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Passwords do not match");
            return;
        }

        // Query DB to check if the user already exists
        UserDAO userDao = new UserDAO(connection);
        try {
            // If the user exists, send back an error message,
            // otherwise create the user and send back a 201 (created) code
            if (userDao.userExists(email)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "An user with this email address already exists");
            } else {
                userDao.createUser(name, surname, email, password);
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred while creating your account");
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