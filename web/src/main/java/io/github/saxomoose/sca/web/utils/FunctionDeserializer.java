// public class FunctionDeserializer implements JsonDeserializer<Function> {
// @Override
// public Function deserialize(JsonElement jsonElement, Type type,
// JsonDeserializationContext
// jsonDeserializationContext) throws JsonParseException {
// if (!jsonElement.getAsJsonObject().has("permanent_representative")) {
// return jsonDeserializationContext.deserialize(jsonElement,
// NaturalPersonFunction.class);
// }
// else {
// return jsonDeserializationContext.deserialize(jsonElement,
// LegalPersonFunction.class);
// }
// }
// }
