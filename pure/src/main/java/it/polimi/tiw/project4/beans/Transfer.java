package it.polimi.tiw.project4.beans;

import java.util.Date;
import java.util.Objects;

public class Transfer {
    private int transferID;
    private Date date;
    private float amount;
    private int sender;
    private int recipient;
    private String reason;

    public Transfer() {
    }

    public Transfer(int transferID, Date date, float amount, int sender, int recipient, String reason) {
        this.transferID = transferID;
        this.date = date;
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
        this.reason = reason;
    }

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transfer transfer = (Transfer) o;
        return transferID == transfer.transferID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferID);
    }
}
