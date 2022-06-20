package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.AccountDAO;
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

@WebServlet("/account/open")
public class OpenAccount extends HttpServlet implements JsonServlet {
    private Connection connection = null;

    public OpenAccount() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");
        float initialBalance;

        try {
            initialBalance = Float.parseFloat(StringEscapeUtils.escapeJava(request.getParameter("balance")));
        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid initial balance provided: not a number");
            return;
        }

        //Checks if balance is positive
        if (initialBalance < 0) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid initial balance provided: negative number");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        try {
            accountDao.createAccount(currentUser, initialBalance);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create the requested account");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
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
