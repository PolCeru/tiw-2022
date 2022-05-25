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

    public List<Account> getAccounts(int userid) throws SQLException {
        String query = "SELECT code, balance " +
                "FROM account NATURAL JOIN user " +
                "WHERE userID = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
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

    public List<Account> getAccounts(User user) throws SQLException {
        return getAccounts(user.getId());
    }

    public void createAccount(int userid) throws SQLException {
        String query = "INSERT INTO account (userID) VALUES (?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
            pstatement.executeUpdate();
        }
    }

    public void createAccount(User user) throws SQLException {
        createAccount(user.getId());
    }

    public Account getAccount(int code) throws SQLException {
        String query = "SELECT code, balance " +
                "FROM account " +
                "WHERE code = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, code);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    throw new SQLException();
                else {
                    result.next();
                    Account account = new Account();
                    account.setCode(result.getInt("code"));
                    account.setBalance(result.getFloat("balance"));
                    return account;
                }
            }
        }
    }
}
