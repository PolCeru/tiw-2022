package it.polimi.tiw.project4.schemas;

import it.polimi.tiw.project4.beans.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountsResponse {
    List<Integer> accounts;

    public AccountsResponse(List<Account> accounts) {
        this.accounts = new ArrayList<>();
        for (Account account : accounts) {
            this.accounts.add(account.getCode());
        }
    }

    public List<Integer> getAccounts() {
        return accounts;
    }
}
