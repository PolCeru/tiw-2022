package it.polimi.tiw.project4.beans;

public class Account {

    private int code;

    private float balance;

    public Account() {
    }

    public Account(int code, float balance) {
        this.code = code;
        this.balance = balance;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
