package it.polimi.tiw.project4.beans;

public class AccountBookEntry {
    private String savedUser;
    private String savedCode;
    private String name;

    public AccountBookEntry() {
    }

    public AccountBookEntry(String savedUser, String savedCode, String name) {
        this.savedUser = savedUser;
        this.savedCode = savedCode;
        this.name = name;
    }

    public String getSavedUser() {
        return savedUser;
    }

    public void setSavedUser(String savedUser) {
        this.savedUser = savedUser;
    }

    public String getSavedCode() {
        return savedCode;
    }

    public void setSavedCode(String savedCode) {
        this.savedCode = savedCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
