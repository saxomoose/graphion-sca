package io.github.saxomoose.sca.web.models.projections.scraper;

import java.io.Serializable;
import java.time.LocalDate;

public class Function implements Serializable {
    private io.github.saxomoose.sca.web.enums.FunctionEnum function;
    private LocalDate startDate;

    public io.github.saxomoose.sca.web.enums.FunctionEnum functionEnum() {
        return function;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public void setFunctionEnum(io.github.saxomoose.sca.web.enums.FunctionEnum function) {
        this.function = function;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
