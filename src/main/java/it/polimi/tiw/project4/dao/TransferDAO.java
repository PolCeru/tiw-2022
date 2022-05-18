package it.polimi.tiw.project4.dao;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {
    private final Connection con;

    public TransferDAO(Connection connection) {
        this.con = connection;
    }

    public List<Transfer> getTransfers(Account account) throws SQLException {
        String query = "SELECT id, date, amount, sender, recipient " +
                "FROM transfer JOIN user " +
                "WHERE sender = ? OR recipient = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, account.getCode());
            pstatement.setInt(2, account.getCode());
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                {
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
}