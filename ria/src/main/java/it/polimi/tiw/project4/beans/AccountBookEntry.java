package it.polimi.tiw.project4.beans;

public class AccountBookEntry {
    private int savedUser;
    private int savedCode;
    private String name;

    public AccountBookEntry() {
    }

    public AccountBookEntry(int savedUser, int savedCode, String name) {
        this.savedUser = savedUser;
        this.savedCode = savedCode;
        this.name = name;
    }

    public int getSavedUser() {
        return savedUser;
    }

    public void setSavedUser(int savedUser) {
        this.savedUser = savedUser;
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
