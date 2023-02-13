package com.saxomoose.eid.web.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.saxomoose.eid.web.dtos.LegalPersonOfficer;
import com.saxomoose.eid.web.dtos.NaturalPersonOfficer;
import com.saxomoose.eid.web.dtos.Officer;

import java.lang.reflect.Type;

public class OfficerDeserializer implements JsonDeserializer<Officer> {
    @Override
    public Officer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.getAsJsonObject().has("last_name")) {
            return jsonDeserializationContext.deserialize(jsonElement, NaturalPersonOfficer.class);
        }
        else {
            return jsonDeserializationContext.deserialize(jsonElement, LegalPersonOfficer.class);
        }
    }
}
