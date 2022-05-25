package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.AccountDAO;
import it.polimi.tiw.project4.utils.ConnectionHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
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
