package io.github.saxomoose.sca.web.models;

import io.github.saxomoose.sca.web.enums.Function;
import org.springframework.data.relational.core.mapping.Table;

@Table(schema = "sca")
public class Officer {
    protected Long legalPerson;
    protected Long signatory;
    protected Function function;
    protected Long enterpriseNumber;
}
