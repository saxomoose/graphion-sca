package com.saxomoose.eid.web.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LegalPersonFunction extends Function implements Serializable {
    @SerializedName("permanent_representative")
    private NaturalPersonOfficer permanentRepresentative;

    public NaturalPersonOfficer getPermanentRepresentative() {
        return permanentRepresentative;
    }

    public void setPermanentRepresentative(NaturalPersonOfficer permanentRepresentative) {
        this.permanentRepresentative = permanentRepresentative;
    }
}
