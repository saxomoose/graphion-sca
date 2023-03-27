package io.github.saxomoose.sca.web.dtos.scraper;

import java.io.Serializable;

public class LegalPersonFunction extends Function implements Serializable {
    private NaturalPersonOfficer permanentRepresentative;

    public NaturalPersonOfficer permanentRepresentative() {
        return permanentRepresentative;
    }

    public void setPermanentRepresentative(NaturalPersonOfficer permanentRepresentative) {
        this.permanentRepresentative = permanentRepresentative;
    }
}
