package it.polimi.tiw.project4.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
    public static Connection getConnection(ServletContext context) throws UnavailableException {
        Connection connection;
        try {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Couldn't find the the JDBC driver class: " + e.getMessage() +
                    ", make sure the connector library is imported correctly");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't open a connection to the DB: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
