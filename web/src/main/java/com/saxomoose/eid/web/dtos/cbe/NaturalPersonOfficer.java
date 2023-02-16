package com.saxomoose.eid.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NaturalPersonOfficer extends Officer implements Serializable {
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
