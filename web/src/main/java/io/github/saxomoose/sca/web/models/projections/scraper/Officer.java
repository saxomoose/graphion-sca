package io.github.saxomoose.sca.web.models.projections.scraper;

import java.io.Serializable;
import java.util.Collection;

public class Officer implements Serializable {
    private Collection<Function> functions;

    public Collection<Function> functions() {
        return functions;
    }

    public void setFunctions(Collection<Function> functions) {
        this.functions = functions;
    }
}
