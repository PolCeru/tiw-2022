package it.polimi.tiw.project4.dao;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {
    private final Connection con;

    public TransferDAO(Connection connection) {
        this.con = connection;
    }

    public List<Transfer> getTransfers(int accountcode) throws SQLException {
        String query = "SELECT ID, date, amount, sender, recipient " +
                "FROM transfer JOIN account s on sender = s.userID " +
                "JOIN account r on recipient = r.userID " +
                "WHERE sender = ? OR recipient = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, accountcode);
            pstatement.setInt(2, accountcode);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) { // no results, no transfers for this account
                    return null;
                } else {
                    ArrayList<Transfer> userTransfers = new ArrayList<>();
                    while (result.next()) {
                        Transfer transfer = new Transfer();
                        transfer.setTransferID(result.getInt("ID"));
                        transfer.setAmount(result.getFloat("amount"));
                        transfer.setDate(result.getDate("date"));
                        transfer.setSender(result.getInt("sender"));
                        transfer.setRecipient(result.getInt("recipient"));
                        userTransfers.add(transfer);
                    }
                    return userTransfers;
                }
            }
        }
    }

    public List<Transfer> getTransfers(Account account) throws SQLException {
        return getTransfers(account.getCode());
    }

    public void createTransfer(int sender, int recipient, String reason, float amount) throws SQLException {
        String query = "INSERT INTO transfer (date, amount, sender, recipient, reason) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setDate(1, new Date(System.currentTimeMillis()));
            pstatement.setFloat(2, amount);
            pstatement.setInt(3, sender);
            pstatement.setInt(4, recipient);
            pstatement.setString(5, reason);
            pstatement.executeUpdate();
        }
        // TODO: update sender and recipient balance with an atomic transaction
    }
}