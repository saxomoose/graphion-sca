package io.github.saxomoose.sca.web.models.dtos.scraper;

import java.io.Serializable;
import java.time.LocalDate;

public class Function implements Serializable {
    private io.github.saxomoose.sca.web.enums.Function function;
    private LocalDate startDate;

    public io.github.saxomoose.sca.web.enums.Function functionEnum() {
        return function;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public void setFunctionEnum(io.github.saxomoose.sca.web.enums.Function function) {
        this.function = function;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
