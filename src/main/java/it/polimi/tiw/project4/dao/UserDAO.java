package it.polimi.tiw.project4.dao;

import it.polimi.tiw.project4.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final Connection con;

    public UserDAO(Connection connection) {
        this.con = connection;
    }

    public User getUser(String email, String password) throws SQLException {
        String query = "SELECT userID, email, name, surname FROM user WHERE email = ? AND password = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, email);
            pstatement.setString(2, password);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setId(result.getInt("userID"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
                    user.setEmail(result.getString("email"));
                    return user;
                }
            }
        }
    }

    public User getUser(int userId) throws SQLException {
        String query = "SELECT userID, email, name, surname FROM user WHERE userID = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, the user doesn't exist
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setId(result.getInt("userID"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
                    user.setEmail(result.getString("email"));
                    return user;
                }
            }
        }
    }

    public void createUser(String name, String surname, String email, String password) throws SQLException {
        String query = "INSERT INTO user (name, surname, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, name);
            pstatement.setString(2, surname);
            pstatement.setString(3, email);
            pstatement.setString(4, password);
            pstatement.executeUpdate();
        }
    }
}
