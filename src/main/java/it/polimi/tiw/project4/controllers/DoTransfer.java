package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.dao.TransferDAO;
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

import static it.polimi.tiw.project4.controllers.TransferErrorMessage.*;

@WebServlet("/do_transfer")
public class DoTransfer extends HttpServlet {
    private Connection connection = null;

    public DoTransfer() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the sender account code from the session
        int senderAccountCode = (int) request.getSession().getAttribute("activeAccount");

        // Obtain and escape params
        String recipientCodeString = StringEscapeUtils.escapeJava(request.getParameter("recipientAccountCode"));
        String recipientAccountCodeString = StringEscapeUtils.escapeJava(request.getParameter("recipientAccountCode"));
        String reasonString = StringEscapeUtils.escapeJava(request.getParameter("reason"));
        String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));

        int recipientCode, recipientAccountCode;
        float amount;
        try {
            recipientCode = Integer.parseInt(recipientCodeString);
            recipientAccountCode = Integer.parseInt(recipientAccountCodeString);
            amount = Float.parseFloat(amountString);
        } catch (NumberFormatException e) {
            String path = getServletContext().getContextPath() + "/account?code=" + senderAccountCode;
            response.sendRedirect(path);
            return;
        }

        UserDAO userDao = new UserDAO(connection);
        AccountDAO accountDao = new AccountDAO(connection);
        TransferDAO transferDao = new TransferDAO(connection);
        int transferCode;
        try {
            // Check that the recipient user account exists
            User recipient = userDao.getUser(recipientCode);
            if (recipient == null) {
                String path = getServletContext().getContextPath() + "/transfer?result=error&code=" + NON_EXISTANT_RECIPIENT.ordinal();
                response.sendRedirect(path);
                return;
            }

            // Check that the recipient account exists
            Account recipientAccount = accountDao.getAccount(recipientAccountCode);
            if (recipientAccount == null) {
                String path = getServletContext().getContextPath() + "/transfer?result=error&code=" + NON_EXISTANT_RECIPIENT_ACCOUNT.ordinal();
                response.sendRedirect(path);
                return;
            }

            // Check that the recipient is not the same as the sender
            if (senderAccountCode == recipientAccountCode) {
                String path = getServletContext().getContextPath() + "/transfer?result=error&code=" + RECIPIENT_SAME_AS_SENDER.ordinal();
                response.sendRedirect(path);
                return;
            }

            // Check that the sender has enough money
            Account senderAccount = accountDao.getAccount(senderAccountCode);
            if (senderAccount.getBalance() < amount) {
                String path = getServletContext().getContextPath() + "/transfer?result=error&code=" + INSUFFICIENT_FUNDS.ordinal();
                response.sendRedirect(path);
                return;
            }

            // All good, execute the transfer
            transferCode = transferDao.createTransfer(senderAccountCode, recipientAccountCode, reasonString, amount);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred while performing the requested transfer");
            return;
        }

        String path = getServletContext().getContextPath() + "/transfer?result=success&code=" + transferCode;
        response.sendRedirect(path);
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

