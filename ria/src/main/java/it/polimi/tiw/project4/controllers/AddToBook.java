package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.AccountBookEntry;
import it.polimi.tiw.project4.dao.AccountBookDAO;
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

@WebServlet("/add_to_book")
public class AddToBook extends HttpServlet implements JsonServlet {
    private Connection connection = null;

    public AddToBook() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");

        // Obtain and escape params
        String accountCodeString = StringEscapeUtils.escapeJava(request.getParameter("code"));
        String name = StringEscapeUtils.escapeJava(request.getParameter("name"));

        int accountCode;
        try {
            accountCode = Integer.parseInt(accountCodeString);
        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to add the account: invalid account code");
            return;
        }

        AccountBookDAO accountBookDao = new AccountBookDAO(connection);
        try {
            AccountBookEntry entry = accountBookDao.getEntry(currentUser, accountCode);
            if (entry == null) {
                accountBookDao.createAccountBookEntry(currentUser, accountCode, name);
                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            }
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to add the account to the user's account book");
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
