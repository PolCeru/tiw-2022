package it.polimi.tiw.project4.dao;

import it.polimi.tiw.project4.beans.AccountBookEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountBookDAO {
    private final Connection con;

    public AccountBookDAO(Connection connection) {
        this.con = connection;
    }

    public List<AccountBookEntry> getAccountBook(int userid) throws SQLException {
        String query = "SELECT account.userID as savedUser, savedCode, name " +
                "FROM account_book book JOIN account ON book.savedCode = account.code " +
                "WHERE book.userID = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) { // no results, no account book entries for this user
                    return null;
                } else {
                    ArrayList<AccountBookEntry> accountBookEntries = new ArrayList<>();
                    while (result.next()) {
                        AccountBookEntry entry = new AccountBookEntry();
                        entry.setSavedUser(Integer.toString(result.getInt("savedUser")));
                        entry.setSavedCode(Integer.toString(result.getInt("savedCode")));
                        entry.setName(result.getString("name"));
                        accountBookEntries.add(entry);
                    }
                    return accountBookEntries;
                }
            }
        }
    }

    public AccountBookEntry getEntry(int userid, int accountId) throws SQLException {
        String query = "SELECT userID, savedCode, name " +
                "FROM account_book " +
                "WHERE userID = ? AND savedCode = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
            pstatement.setInt(2, accountId);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) { // no results, the entry does not exist for this user
                    return null;
                } else {
                    result.next();
                    AccountBookEntry entry = new AccountBookEntry();
                    entry.setSavedUser(Integer.toString(result.getInt("userID")));
                    entry.setSavedCode(Integer.toString(result.getInt("savedCode")));
                    entry.setName(result.getString("name"));
                    return entry;
                }
            }
        }
    }

    public void createAccountBookEntry(int userid, int savedCode, String name) throws SQLException {
        String query = "INSERT INTO account_book (userID, savedCode, name) VALUES (?, ?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userid);
            pstatement.setInt(2, savedCode);
            pstatement.setString(3, name);
            pstatement.executeUpdate();
        }
    }
}
