package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.AccountBookEntry;
import it.polimi.tiw.project4.dao.AccountBookDAO;
import it.polimi.tiw.project4.schemas.AccountBookResponse;
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
import java.util.List;

@WebServlet("/account_book")
public class GetAccountBook extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<AccountBookResponse> responseAdapter;

    public GetAccountBook() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        responseAdapter = JsonHelper.getJsonAdapter(AccountBookResponse.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");

        AccountBookDAO accountBookDao = new AccountBookDAO(connection);
        List<AccountBookEntry> bookEntries;
        try {
            bookEntries = accountBookDao.getAccountBook(currentUser);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve user's account book");
            return;
        }

        String jsonResponse = responseAdapter.toJson(new AccountBookResponse(bookEntries));
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
