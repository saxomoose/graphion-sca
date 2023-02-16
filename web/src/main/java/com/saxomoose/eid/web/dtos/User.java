package com.saxomoose.eid.web.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class User {
    @NotNull
    @Email
    private String username;

    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
