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

    public User checkCredentials(String email, String pwd) throws SQLException {
        String query = "SELECT id, email, name, surname FROM user WHERE email = ? AND password = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, email);
            pstatement.setString(2, pwd);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setId(result.getInt("id"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
                    user.setEmail(result.getString("email"));
                    return user;
                }
            }
        }
    }
}
