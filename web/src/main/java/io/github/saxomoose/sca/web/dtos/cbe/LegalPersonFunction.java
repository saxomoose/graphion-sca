package io.github.saxomoose.sca.web.dtos.cbe;

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
