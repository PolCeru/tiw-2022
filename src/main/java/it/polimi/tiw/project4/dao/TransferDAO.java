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
        String query = "SELECT * " +
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
                        transfer.setReason(result.getString("reason"));
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

    public Transfer getTransfer(int transferCode) throws SQLException {
        String query = "SELECT * FROM transfer WHERE ID = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, transferCode);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) { // no results, transfer does not exist
                    return null;
                } else {
                    result.next();
                    Transfer transfer = new Transfer();
                    transfer.setTransferID(result.getInt("ID"));
                    transfer.setAmount(result.getFloat("amount"));
                    transfer.setDate(result.getDate("date"));
                    transfer.setSender(result.getInt("sender"));
                    transfer.setRecipient(result.getInt("recipient"));
                    transfer.setReason(result.getString("reason"));
                    return transfer;
                }
            }
        }
    }

    public int createTransfer(int sender, int recipient, String reason, float amount) throws SQLException {
        int transferId;
        String query = "INSERT INTO transfer (date, amount, sender, recipient, reason) VALUES (?, ?, ?, ?, ?)";
        try {
            // We need to create a new transfer entry and update the sender and recipient balances
            // Disable auto-commit to allow multiple statements to be executed as a single transaction
            con.setAutoCommit(false);
            try (PreparedStatement pstatement = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstatement.setDate(1, new Date(System.currentTimeMillis()));
                pstatement.setFloat(2, amount);
                pstatement.setInt(3, sender);
                pstatement.setInt(4, recipient);
                pstatement.setString(5, reason);
                pstatement.executeUpdate();
                try (ResultSet result = pstatement.getGeneratedKeys()) {
                    if (!result.isBeforeFirst()) { // no results, transfer was not created
                        return 0;
                    } else {
                        result.next();
                        transferId = result.getInt(1);
                    }
                }
            }
            // Update sender and recipient balances
            // We pass the same connection as this DAO to ensure the update will be executed in the same transaction
            AccountDAO accountDao = new AccountDAO(con);
            accountDao.updateBalanceForTransfer(sender, recipient, amount);

            // Commit the transaction
            con.commit();
        } catch (SQLException e) {
            // Rollback the transaction
            con.rollback();
            throw e;
        } finally {
            // Re-enable auto-commit to ensure future transactions on this connections are not affected
            con.setAutoCommit(true);
        }
        return transferId;
    }
}