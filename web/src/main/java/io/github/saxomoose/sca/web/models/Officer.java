package io.github.saxomoose.sca.web.models;

import org.springframework.data.relational.core.mapping.Table;
import io.github.saxomoose.sca.web.enums.Function;

@Table(schema = "sca")
public class Officer {
    private final Long legalPerson;
    private final Long signatory;
    private final Function function;
    private final Long enterpriseNumber;

    public Officer(Long legalPerson, Long signatory, Function function, Long enterpriseNumber) {
        this.legalPerson = legalPerson;
        this.signatory = signatory;
        this.function = function;
        this.enterpriseNumber = enterpriseNumber;
    }

    public Long legalPerson() {
        return legalPerson;
    }

    public Long signatory() {
        return signatory;
    }

    public Function function() {
        return function;
    }

    public Long enterpriseNumber() {
        return enterpriseNumber;
    }

    
    
}
