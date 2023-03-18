package io.github.saxomoose.sca.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Function implements Serializable {
    @SerializedName("function")
    private io.github.saxomoose.sca.web.enums.Function function;
    @SerializedName("start_date")
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
