package io.github.saxomoose.sca.web.models.projections.scraper;

public class NaturalPersonOfficer extends Officer {
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
