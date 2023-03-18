package io.github.saxomoose.sca.web.dtos;

import jakarta.validation.constraints.Pattern;

public class User {

    // OWASP email regex pattern
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    private String username;

    // - Min 8 characters
    // - Must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number
    // - Can contain special characters
    // (- Max 128 characters)
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,128}$")
    private String password;

    public String username() {
        return username;
    }
    public String password() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
