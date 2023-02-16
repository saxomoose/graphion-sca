package com.saxomoose.eid.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Function implements Serializable {
    @SerializedName("function")
    private com.saxomoose.eid.web.enums.Function function;
    @SerializedName("start_date")
    private LocalDate startDate;

    public com.saxomoose.eid.web.enums.Function getFunctionEnum() {
        return function;
    }

    public void setFunctionEnum(com.saxomoose.eid.web.enums.Function function) {
        this.function = function;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
