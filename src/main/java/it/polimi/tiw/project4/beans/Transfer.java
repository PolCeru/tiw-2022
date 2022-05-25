package it.polimi.tiw.project4.beans;

import java.util.Date;

public class Transfer {

    private int transferID;

    private Date date;

    private float amount;

    private int sender;

    private int recipient;

    public Transfer() {
    }

    public Transfer(int transferID, Date date, float amount, int sender, int recipient) {
        this.transferID = transferID;
        this.date = date;
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
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

    public int getFrom() {
        return sender;
    }

    public void setFrom(int sender) {
        this.sender = sender;
    }

    public int getTo() {
        return recipient;
    }

    public void setTo(int recipient) {
        this.recipient = recipient;
    }
}
