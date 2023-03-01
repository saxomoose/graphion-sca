package sca.web.dtos.cbe;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Function implements Serializable {
    @SerializedName("function")
    private sca.web.enums.Function function;
    @SerializedName("start_date")
    private LocalDate startDate;

    public sca.web.enums.Function getFunctionEnum() {
        return function;
    }

    public void setFunctionEnum(sca.web.enums.Function function) {
        this.function = function;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
