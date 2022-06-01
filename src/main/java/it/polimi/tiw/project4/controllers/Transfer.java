package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.dao.TransferDAO;
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

@WebServlet("/transfer")
public class Transfer extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public Transfer() {
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
        // Get the account code from the url, which will be used as the sender account code
        int senderCode;
        try {
            senderCode = Integer.parseInt(request.getParameter("code"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid account code: not a number");
            return;
        }
        // TODO: check that the current account code is owned by the current user

        // Obtain and escape params
        String accountCodeString = StringEscapeUtils.escapeJava(request.getParameter("accountCode"));
        String reasonString = StringEscapeUtils.escapeJava(request.getParameter("reason"));
        String amountString = StringEscapeUtils.escapeJava(request.getParameter("amount"));

        int recipientCode;
        float amount;
        try {
            recipientCode = Integer.parseInt(accountCodeString);
            amount = Float.parseFloat(amountString);
        } catch (NumberFormatException e) {
            // TODO: use form error message field instead of sending BAD_REQUEST
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transfer parameters provided: not a number");
            return;
        }

        TransferDAO transferDao = new TransferDAO(connection);
        try {
            transferDao.createTransfer(senderCode, recipientCode, reasonString, amount);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to execute the requested transfer");
            return;
        }

        // TODO: redirect to transfer confirmed page
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

