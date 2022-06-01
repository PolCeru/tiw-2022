package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import it.polimi.tiw.project4.utils.TemplateEngineHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int userid;
        try {
            userid = (int) request.getSession().getAttribute("userid");
        } catch (NullPointerException e) {
            // If we catch a NPE, it means that the user is not logged in
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is not authenticated");
            return;
        }

        float initialBalance;
        try {
            initialBalance = Float.parseFloat(StringEscapeUtils.escapeJava(request.getParameter("balance")));
        } catch (NumberFormatException e) {
            // TODO: use form error message field instead of sending BAD_REQUEST
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid initial balance provided: not a number");
            return;
        }

        AccountDAO accountDao = new AccountDAO(connection);
        try {
            accountDao.createAccount(userid, initialBalance);
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
