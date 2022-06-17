package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;
import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.dao.TransferDAO;
import it.polimi.tiw.project4.dao.UserDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.TemplateEngineHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/transfer")
public class TransferResult extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public TransferResult() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TransferStatus transferStatus;
        int resultCode;
        try {
            transferStatus = TransferStatus.parseStatus(request.getParameter("result"));
            resultCode = Integer.parseInt(request.getParameter("code"));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transfer parameters");
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        int activeAccount = (int) request.getSession().getAttribute("activeAccount");
        ctx.setVariable("backButton", ctx.getServletContext().getContextPath() + "/account?code=" + activeAccount);
        ctx.setVariable("activeAccount", activeAccount);

        switch (transferStatus) {
            case SUCCESS -> {
                TransferDAO transferDao = new TransferDAO(connection);
                Transfer transfer;
                try {
                    transfer = transferDao.getTransfer(resultCode);
                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve the requested transfer");
                    return;
                }
                // Check that the transfer exists
                if (transfer == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested transfer does not exist");
                    return;
                }

                UserDAO userDao = new UserDAO(connection);
                AccountDAO accountDao = new AccountDAO(connection);
                User sender, recipient;
                Account senderAccount, recipientAccount;
                try {
                    senderAccount = accountDao.getAccount(transfer.getSender());
                    recipientAccount = accountDao.getAccount(transfer.getRecipient());
                    sender = userDao.getUser(senderAccount.getUserID());
                    recipient = userDao.getUser(recipientAccount.getUserID());
                } catch (SQLException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve the requested transfer details");
                    return;
                }

                // Populate transfer details
                ctx.setVariable("transferCode", resultCode);
                ctx.setVariable("amount", transfer.getAmount());
                ctx.setVariable("reason", transfer.getReason());
                ctx.setVariable("date", transfer.getDate());

                // Populate sender details
                ctx.setVariable("senderUserCode", sender.getId());
                ctx.setVariable("senderAccountCode", transfer.getSender());
                ctx.setVariable("senderBalanceBefore", senderAccount.getBalance() + transfer.getAmount());
                ctx.setVariable("senderBalanceAfter", senderAccount.getBalance());

                // Populate recipient details
                ctx.setVariable("recipientUserCode", recipient.getId());
                ctx.setVariable("recipientAccountCode", transfer.getRecipient());
                ctx.setVariable("recipientBalanceBefore", recipientAccount.getBalance() - transfer.getAmount());
                ctx.setVariable("recipientBalanceAfter", recipientAccount.getBalance());

                templateEngine.process("/transfer_success.html", ctx, response.getWriter());
            }
            case ERROR -> {
                ctx.setVariable("errorMsg", TransferErrorMessage.parseCode(resultCode).message);
                templateEngine.process("/transfer_error.html", ctx, response.getWriter());
            }
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


