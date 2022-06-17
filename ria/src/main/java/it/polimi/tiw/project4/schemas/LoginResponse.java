package it.polimi.tiw.project4.schemas;

import it.polimi.tiw.project4.beans.User;

public class LoginResponse {
    private String name;
    private int id;

    public LoginResponse(User user) {
        this.name = user.getName();
        this.id = user.getId();
    }

    public LoginResponse(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
