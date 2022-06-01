package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;
import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.dao.TransferDAO;
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
import java.util.List;

@WebServlet("/account")
public class ShowAccount extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public ShowAccount() {
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int accountCode;
        try {
            accountCode = Integer.parseInt(request.getParameter("code"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid account code: not a number");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        Account account;
        try {
            account = accountDao.getAccount(accountCode);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to retrieve the selected account");
            return;
        }

        TransferDAO transferDao = new TransferDAO(connection);
        List<Transfer> transferList;
        try {
            transferList = transferDao.getTransfers(account);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to retrieve transfers for the selected account");
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("accountID", account.getCode());
        ctx.setVariable("balance", account.getBalance());
        ctx.setVariable("transfers", transferList);
        templateEngine.process("/account.html", ctx, response.getWriter());
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
