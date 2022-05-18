package it.polimi.tiw.project4.beans;

import java.util.Date;

public class Transfer {

    private int transferID;

    private Date date;

    private float amount;

    private int from;

    private int to;

    public Transfer() {
    }

    public Transfer(int transferID, Date date, float amount, int from, int to) {
        this.transferID = transferID;
        this.date = date;
        this.amount = amount;
        this.from = from;
        this.to = to;
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
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
