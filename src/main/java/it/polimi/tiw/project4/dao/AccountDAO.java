package it.polimi.tiw.project4.dao;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final Connection con;

    public AccountDAO(Connection connection) {
        this.con = connection;
    }

    public List<Account> getAccounts(User user) throws SQLException {
        String query = "SELECT code, balance " +
                "FROM account NATURAL JOIN user " +
                "WHERE userID = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, user.getId());
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                {
                    return null;
                } else {
                    ArrayList<Account> userAccounts = new ArrayList<>();
                    while (result.next()) {
                        Account account = new Account();
                        account.setCode(result.getInt("code"));
                        account.setBalance(result.getFloat("balance"));
                        userAccounts.add(account);
                    }
                    return userAccounts;
                }
            }
        }
    }
}