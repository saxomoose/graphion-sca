package com.saxomoose.eid.web.dtos.cbe;

import java.io.Serializable;
import java.util.Collection;

public class Officer implements Serializable {
    private Collection<Function> functions;

    public Collection<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(Collection<Function> functions) {
        this.functions = functions;
    }
}
