package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.Transfer;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.dao.TransferDAO;
import it.polimi.tiw.project4.schemas.TransfersResponse;
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

@WebServlet("/transfer")
public class GetTransfers extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<TransfersResponse> responseAdapter;

    public GetTransfers() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        responseAdapter = JsonHelper.getJsonAdapter(TransfersResponse.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int currentUser = (int) request.getSession().getAttribute("currentUser");
        int account;
        try {
            account = Integer.parseInt(request.getParameter("account"));
        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to retrieve transfers: invalid account code");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        TransferDAO transferDao = new TransferDAO(connection);
        List<Transfer> transferList;
        try {
            // Check if the user is the owner of the requested account
            if (accountDao.getAccountFromUserID(currentUser, account) == null) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Failed to retrieve transfers of an account that doesn't exist for the current user");
                return;
            }
            transferList = transferDao.getTransfers(account);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve transfers for the selected account");
            return;
        }

        String jsonResponse = responseAdapter.toJson(new TransfersResponse(transferList));
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
