//public class OfficerDeserializer implements JsonDeserializer<Officer> {
//    @Override
//    public Officer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//        if (jsonElement.getAsJsonObject().has("last_name")) {
//            return jsonDeserializationContext.deserialize(jsonElement, NaturalPersonOfficer.class);
//        }
//        else {
//            return jsonDeserializationContext.deserialize(jsonElement, LegalPersonOfficer.class);
//        }
//    }
//}
