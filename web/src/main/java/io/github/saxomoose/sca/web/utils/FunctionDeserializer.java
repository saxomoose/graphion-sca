package io.github.saxomoose.sca.web.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.saxomoose.sca.web.dtos.cbe.NaturalPersonFunction;
import io.github.saxomoose.sca.web.dtos.cbe.Function;
import io.github.saxomoose.sca.web.dtos.cbe.LegalPersonFunction;

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
