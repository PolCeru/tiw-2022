package it.polimi.tiw.project4.schemas;

import it.polimi.tiw.project4.beans.AccountBookEntry;

import java.util.List;

public class AccountBookResponse {
    private List<AccountBookEntry> bookEntries;

    public AccountBookResponse(List<AccountBookEntry> bookEntries) {
        this.bookEntries = bookEntries;
    }
}
