package it.polimi.tiw.project4.beans;

public class AccountBookEntry {
    private int userID;
    private int savedCode;
    private String name;

    public AccountBookEntry() {
    }

    public AccountBookEntry(int userID, int savedCode, String name) {
        this.userID = userID;
        this.savedCode = savedCode;
        this.name = name;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getSavedCode() {
        return savedCode;
    }

    public void setSavedCode(int savedCode) {
        this.savedCode = savedCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
