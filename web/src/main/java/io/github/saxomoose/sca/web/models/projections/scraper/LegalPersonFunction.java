package io.github.saxomoose.sca.web.models.projections.scraper;

public class LegalPersonFunction extends Function {
    private NaturalPersonOfficer permanentRepresentative;

    public NaturalPersonOfficer permanentRepresentative() {
        return permanentRepresentative;
    }

    public void setPermanentRepresentative(NaturalPersonOfficer permanentRepresentative) {
        this.permanentRepresentative = permanentRepresentative;
    }
}
