package io.github.saxomoose.sca.web.models.projections.scraper;

import java.io.Serializable;
import java.util.Collection;

public class Enterprise implements Serializable {
    private Integer enterpriseNumber;
    private Collection<Officer> officers;
}
