package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.User;
import it.polimi.tiw.project4.dao.AccountDAO;
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
import java.util.List;

@WebServlet("/home")
public class Home extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public Home() {
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
        int currentUser = (int) request.getSession().getAttribute("currentUser");
        UserDAO userDao = new UserDAO(connection);
        User user;
        try {
            user = userDao.getUser(currentUser);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve user info");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        List<Account> userAccounts;
        try {
            userAccounts = accountDao.getAccounts(currentUser);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve user's accounts");
            return;
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("name", user.getName());
        ctx.setVariable("accounts", userAccounts);
        templateEngine.process("/home.html", ctx, response.getWriter());
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
