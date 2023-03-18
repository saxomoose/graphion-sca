package io.github.saxomoose.sca.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NaturalPersonOfficer extends Officer implements Serializable {
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
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
