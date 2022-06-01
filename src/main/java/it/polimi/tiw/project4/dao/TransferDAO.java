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
                "FROM transfer JOIN user " +
                "WHERE sender = ? OR recipient = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, accountcode);
            pstatement.setInt(2, accountcode);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) {
                    // no results, credential check failed
                    return null;
                } else {
                    ArrayList<Transfer> userTransfers = new ArrayList<>();
                    while (result.next()) {
                        Transfer transfer = new Transfer();
                        transfer.setTransferID(result.getInt("ID"));
                        transfer.setAmount(result.getFloat("amount"));
                        transfer.setDate(result.getDate("date"));
                        transfer.setFrom(result.getInt("from"));
                        transfer.setTo(result.getInt("to"));
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
        String query = "INSERT INTO transfer (date, amount, sender, recipient) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setDate(1, new Date(System.currentTimeMillis()));
            pstatement.setFloat(2, amount);
            pstatement.setInt(3, sender);
            pstatement.setInt(4, recipient);
            pstatement.executeUpdate();
        }
        // TODO: update sender and recipient balance with an atomic transaction
    }
}