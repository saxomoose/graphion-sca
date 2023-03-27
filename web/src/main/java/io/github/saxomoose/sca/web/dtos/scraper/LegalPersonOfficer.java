package io.github.saxomoose.sca.web.dtos.scraper;

import java.io.Serializable;

public class LegalPersonOfficer extends Officer implements Serializable {
    private Integer enterpriseNumber;

    public Integer enterpriseNumber() {
        return enterpriseNumber;
    }

    public void setEnterpriseNumber(Integer enterpriseNumber) {
        this.enterpriseNumber = enterpriseNumber;
    }
}
