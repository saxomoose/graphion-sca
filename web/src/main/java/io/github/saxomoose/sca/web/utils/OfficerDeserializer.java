package io.github.saxomoose.sca.web.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.saxomoose.sca.web.dtos.cbe.LegalPersonOfficer;
import io.github.saxomoose.sca.web.dtos.cbe.NaturalPersonOfficer;
import io.github.saxomoose.sca.web.dtos.cbe.Officer;

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
