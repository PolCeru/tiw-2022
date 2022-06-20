package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.JsonHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/get_account")
public class GetAccountInfo extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<Account> responseAdapter;

    public GetAccountInfo() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        responseAdapter = JsonHelper.getJsonAdapter(Account.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");
        int accountId;
        try {
            accountId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        Account account;
        try {
            account = accountDao.getAccountFromUserID(accountId, currentUser);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve the requested account's information");
            return;
        }

        if (account == null) {
            sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "The requested account does not exist for the current user");
            return;
        }

        String jsonResponse = responseAdapter.toJson(account);
        response.setStatus(HttpServletResponse.SC_OK);
        sendJson(response, jsonResponse);
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
