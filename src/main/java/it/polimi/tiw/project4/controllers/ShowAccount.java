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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int accountCode = 0;
        Account account = null;
        AccountDAO accountDao = new AccountDAO(connection);
        try {
            accountCode = Integer.parseInt(request.getParameter("code"));
            account = accountDao.getAccount(accountCode);
        } catch (NumberFormatException ignored) {
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to retrieve the selected account");
            return;
        }

        // Check that the requested account exists
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "The requested account does not exist");
            return;
        }

        // Check that the requested account belongs to the user
        int currentUser = (int) request.getSession().getAttribute("currentUser");
        try {
            if (!accountDao.getAccounts(currentUser).contains(account)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "The requested account does not belong to the user");
                return;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to validate the user account");
            return;
        }

        request.getSession().setAttribute("activeAccount", accountCode);

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
        ctx.setVariable("backButton", ctx.getServletContext().getContextPath() + "/home");
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
