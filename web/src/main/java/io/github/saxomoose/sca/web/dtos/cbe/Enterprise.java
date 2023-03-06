package io.github.saxomoose.sca.web.dtos.cbe;

import java.io.Serializable;
import java.util.Collection;

public class Enterprise implements Serializable {
    private Integer enterpriseNumber;
    private Collection<Officer> officers;
}
