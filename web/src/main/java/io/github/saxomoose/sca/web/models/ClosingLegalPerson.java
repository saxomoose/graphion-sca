package io.github.saxomoose.sca.web.models;

import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class ClosingLegalPerson {
    protected Long legalPerson;

//    Is the legal person validly represented? 2 Requirements.
//    @Column(columnDefinition = "boolean default false")
    private Boolean status = Boolean.FALSE;

    public Boolean status() {
        return status;
    }

    public void setStatus(Boolean status) { this.status = status; }
}
