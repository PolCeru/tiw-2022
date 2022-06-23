package it.polimi.tiw.project4.controllers;

import com.squareup.moshi.JsonAdapter;
import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;
import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.dao.TransferDAO;
import it.polimi.tiw.project4.dao.UserDAO;
import it.polimi.tiw.project4.schemas.NewTransferResponse;
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

import static it.polimi.tiw.project4.controllers.TransferErrorMessage.*;

@WebServlet("/do_transfer")
public class DoTransfer extends HttpServlet implements JsonServlet {
    private Connection connection = null;
    private JsonAdapter<NewTransferResponse> responseAdapter;

    public DoTransfer() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.responseAdapter = JsonHelper.getJsonAdapter(NewTransferResponse.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtain and escape params
        String senderAccountCodeString = StringEscapeUtils.escapeJava(request.getParameter("senderAccountCode"));
        String recipientCodeString = StringEscapeUtils.escapeJava(request.getParameter("recipientCode"));
        String recipientAccountCodeString = StringEscapeUtils.escapeJava(request.getParameter("recipientAccountCode"));
        String reasonString = StringEscapeUtils.escapeJava(request.getParameter("reason"));
        String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));

        int senderAccountCode, recipientCode, recipientAccountCode;
        float amount;
        try {
            senderAccountCode = Integer.parseInt(senderAccountCodeString);
            recipientCode = Integer.parseInt(recipientCodeString);
            recipientAccountCode = Integer.parseInt(recipientAccountCodeString);
            amount = Float.parseFloat(amountString);
        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to perform transfer: invalid recipient code or amount");
            return;
        }

        UserDAO userDao = new UserDAO(connection);
        AccountDAO accountDao = new AccountDAO(connection);
        TransferDAO transferDao = new TransferDAO(connection);
        Account senderAccount, recipientAccount;
        Transfer transfer;
        try {
            // Check that the recipient user account exists
            User recipient = userDao.getUser(recipientCode);
            if (recipient == null) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, NON_EXISTANT_RECIPIENT.message);
                return;
            }

            // Check that the recipient account exists
            recipientAccount = accountDao.getAccountFromUserID(recipientAccountCode, recipient.getId());
            if (recipientAccount == null) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, NON_EXISTANT_RECIPIENT_ACCOUNT.message);
                return;
            }
            // Check that the recipient is not the same as the sender
            senderAccount = accountDao.getAccount(senderAccountCode);
            if (recipientCode == senderAccount.getCode() && senderAccountCode == recipientAccountCode) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, RECIPIENT_SAME_AS_SENDER.message);
                return;
            }

            // Check that the amount is not negative
            if (amount < 0) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, NEGATIVE_AMOUNT.message);
                return;
            }

            // Check that the sender has enough money
            if (senderAccount.getBalance() < amount) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, INSUFFICIENT_FUNDS.message);
                return;
            }

            // Check that the reason is not too long
            if (reasonString.length() > 150) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, REASON_TOO_LONG.message);
                return;
            }

            // All good, execute the transfer
            int transferCode = transferDao.createTransfer(senderAccountCode, recipientAccountCode, reasonString, amount);
            transfer = transferDao.getTransfer(transferCode);
        } catch (SQLException e) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred while performing the requested transfer");
            return;
        }

        // We don't update senderAccount and recipientAccount, so they still hold the balance value pre-transaction
        String jsonResponse = responseAdapter.toJson(new NewTransferResponse(transfer, senderAccount, recipientAccount));
        response.setStatus(HttpServletResponse.SC_CREATED);
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

