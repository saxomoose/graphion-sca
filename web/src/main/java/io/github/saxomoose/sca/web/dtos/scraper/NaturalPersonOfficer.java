package io.github.saxomoose.sca.web.dtos.scraper;

import java.io.Serializable;

public class NaturalPersonOfficer extends Officer implements Serializable {
    private String firstName;
    private String lastName;

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
