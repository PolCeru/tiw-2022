package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.TemplateEngineHandler;
import org.apache.commons.text.StringEscapeUtils;
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

@WebServlet("/account/open")
public class OpenAccount extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public OpenAccount() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.connection = ConnectionHandler.getConnection(servletContext);
        this.templateEngine = TemplateEngineHandler.getEngine(servletContext);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        int currentUser = (int) request.getSession().getAttribute("currentUser");

        float initialBalance;
        try {
            initialBalance = Float.parseFloat(StringEscapeUtils.escapeJava(request.getParameter("balance")));
        } catch (NumberFormatException e) {
            /*ctx.setVariable("errorMsg", "Invalid initial balance, not a number");
            templateEngine.process("../home.html", ctx, response.getWriter());*/
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid initial balance provided: not a number");
            return;
        }

        //Checks if balance is positive
        if (initialBalance < 0) {
            /*ctx.setVariable("errorMsg", "Invalid initial balance, not positive");
            templateEngine.process("../home.html", ctx, response.getWriter());*/
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid initial balance provided: negative number");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        try {
            accountDao.createAccount(currentUser, initialBalance);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create the requested account");
            return;
        }

        String path = getServletContext().getContextPath() + "/home";
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
