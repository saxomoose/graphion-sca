package sca.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LegalPersonOfficer extends Officer implements Serializable {
    @SerializedName("enterprise_number")
    private Integer enterpriseNumber;

    public Integer getEnterpriseNumber() {
        return enterpriseNumber;
    }

    public void setEnterpriseNumber(Integer enterpriseNumber) {
        this.enterpriseNumber = enterpriseNumber;
    }
}
