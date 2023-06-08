package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.FunctionEnum;

public class Officer {
    private final Long legalPerson;
    private final Long signatory;
    private final FunctionEnum function;
    private final Long enterpriseNumber;

    public Officer(Long legalPerson, Long signatory, FunctionEnum function, Long enterpriseNumber) {
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

    public FunctionEnum function() {
        return function;
    }

    public Long enterpriseNumber() {
        return enterpriseNumber;
    }

    
    
}
