package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.schemas.AccountsResponse;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/account")
public class GetAccounts extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<AccountsResponse> responseAdapter;

    public GetAccounts() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        responseAdapter = JsonHelper.getJsonAdapter(AccountsResponse.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");

        // Query DB to get the user accounts
        AccountDAO accountDao = new AccountDAO(connection);
        List<Account> userAccounts;
        try {
            userAccounts = accountDao.getAccounts(currentUser);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve user's accounts");
            return;
        }

        if (userAccounts == null) {
            userAccounts = new ArrayList<>();
        }

        String jsonResponse = responseAdapter.toJson(new AccountsResponse(userAccounts));
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
