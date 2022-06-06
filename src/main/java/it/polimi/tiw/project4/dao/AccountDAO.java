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
                if (!result.isBeforeFirst()) { // no results, no accounts for this user
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

    public void createAccount(int userid, float balance) throws SQLException {
        String query = "INSERT INTO account (userID, balance) VALUES (?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
            pstatement.setFloat(2, balance);
            pstatement.executeUpdate();
        }
    }

    public void createAccount(User user, float balance) throws SQLException {
        createAccount(user.getId(), balance);
    }

    public Account getAccount(int code) throws SQLException {
        String query = "SELECT code, balance " +
                "FROM account " +
                "WHERE code = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, code);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, the account doesn't exist
                    return null;
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

    public void updateBalanceForTransfer(int sender, int recipient, float amount) throws SQLException {
        String senderQuery = "UPDATE account SET balance = (balance - ?) WHERE code = ?";
        try (PreparedStatement pstatement = con.prepareStatement(senderQuery)) {
            pstatement.setFloat(1, amount);
            pstatement.setInt(2, sender);
            pstatement.executeUpdate();
        }

        String recipientQuery = "UPDATE account SET balance = (balance + ?) WHERE code = ?";
        try (PreparedStatement pstatement = con.prepareStatement(recipientQuery)) {
            pstatement.setFloat(1, amount);
            pstatement.setInt(2, recipient);
            pstatement.executeUpdate();
        }
    }
}
