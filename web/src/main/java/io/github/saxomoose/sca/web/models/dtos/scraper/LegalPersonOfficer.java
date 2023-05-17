package io.github.saxomoose.sca.web.models.dtos.scraper;

public class LegalPersonOfficer extends Officer {
    private Integer enterpriseNumber;

    public Integer enterpriseNumber() {
        return enterpriseNumber;
    }

    public void setEnterpriseNumber(Integer enterpriseNumber) {
        this.enterpriseNumber = enterpriseNumber;
    }
}
