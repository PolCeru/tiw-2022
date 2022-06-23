package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.UserDAO;
import it.polimi.tiw.project4.schemas.LoginResponse;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.JsonHelper;
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

@WebServlet("/login")
public class Login extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<LoginResponse> responseAdapter;

    public Login() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        responseAdapter = JsonHelper.getJsonAdapter(LoginResponse.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtain and escape params
        String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Please provide valid credentials");
            return;
        }

        // Query DB to authenticate the user
        UserDAO userDao = new UserDAO(connection);
        User user;
        try {
            user = userDao.getUser(email, password);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred while checking your credentials");
            return;
        }

        // If the user exists, add info to the session and send back the user info,
        // otherwise send back an error message
        if (user == null) {
            sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid email or password");
        } else {
            request.getSession().setAttribute("currentUser", user.getId());
            String jsonResponse = responseAdapter.toJson(new LoginResponse(user));
            response.setStatus(HttpServletResponse.SC_OK);
            sendJson(response, jsonResponse);
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