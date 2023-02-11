package com.saxomoose.eid.web.dtos;

import com.google.gson.annotations.SerializedName;
import com.saxomoose.eid.web.enums.FunctionEnum;

import java.io.Serializable;
import java.util.Date;

public class Function implements Serializable {
    @SerializedName("function")
    FunctionEnum functionEnum;
    @SerializedName("start_date")
    Date startDate;

    public FunctionEnum getFunctionEnum() {
        return functionEnum;
    }

    public void setFunctionEnum(FunctionEnum functionEnum) {
        this.functionEnum = functionEnum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
