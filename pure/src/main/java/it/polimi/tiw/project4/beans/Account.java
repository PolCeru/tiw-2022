package it.polimi.tiw.project4.beans;

import java.util.Objects;

public class Account {
    private int code;

    private int userID;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return code == account.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
