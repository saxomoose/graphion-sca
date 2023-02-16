package com.saxomoose.eid.web.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.saxomoose.eid.web.dtos.cbe.Function;
import com.saxomoose.eid.web.dtos.cbe.LegalPersonFunction;
import com.saxomoose.eid.web.dtos.cbe.NaturalPersonFunction;

import java.lang.reflect.Type;

public class FunctionDeserializer implements JsonDeserializer<Function> {
    @Override
    public Function deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonElement.getAsJsonObject().has("permanent_representative")) {
            return jsonDeserializationContext.deserialize(jsonElement, NaturalPersonFunction.class);
        }
        else {
            return jsonDeserializationContext.deserialize(jsonElement, LegalPersonFunction.class);
        }
    }
}
