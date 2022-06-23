package it.polimi.tiw.project4.schemas;

import it.polimi.tiw.project4.beans.Account;
import it.polimi.tiw.project4.beans.Transfer;

import java.util.Date;

public class NewTransferResponse {
    private int transferCode;
    private float amount;
    private String reason;
    private Date date;

    private int senderUserCode;
    private int senderAccountCode;
    private float senderBalanceBefore;
    private float senderBalanceAfter;

    private int recipientUserCode;
    private int recipientAccountCode;
    private float recipientBalanceBefore;
    private float recipientBalanceAfter;

    /**
     * @apiNote An assumption is made about the sender and recipient parameters: they hold the balance value pre-transaction.
     */
    public NewTransferResponse(Transfer transfer, Account sender, Account recipient) {
        this.transferCode = transfer.getTransferID();
        this.amount = transfer.getAmount();
        this.reason = transfer.getReason();
        this.date = transfer.getDate();

        this.senderUserCode = sender.getUserID();
        this.senderAccountCode = transfer.getSender();
        this.senderBalanceBefore = sender.getBalance();
        this.senderBalanceAfter = sender.getBalance() - transfer.getAmount();

        this.recipientUserCode = recipient.getUserID();
        this.recipientAccountCode = transfer.getRecipient();
        this.recipientBalanceBefore = recipient.getBalance();
        this.recipientBalanceAfter = recipient.getBalance() + transfer.getAmount();
    }

    public int getTransferCode() {
        return transferCode;
    }

    public float getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public Date getDate() {
        return date;
    }

    public int getSenderUserCode() {
        return senderUserCode;
    }

    public int getSenderAccountCode() {
        return senderAccountCode;
    }

    public float getSenderBalanceBefore() {
        return senderBalanceBefore;
    }

    public float getSenderBalanceAfter() {
        return senderBalanceAfter;
    }

    public int getRecipientUserCode() {
        return recipientUserCode;
    }

    public int getRecipientAccountCode() {
        return recipientAccountCode;
    }

    public float getRecipientBalanceBefore() {
        return recipientBalanceBefore;
    }

    public float getRecipientBalanceAfter() {
        return recipientBalanceAfter;
    }
}
