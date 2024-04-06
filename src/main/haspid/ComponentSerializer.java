package main.haspid;

import com.google.gson.*;
import main.components.Component;

import java.lang.reflect.Type;

public class ComponentSerializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        String clazz = jObject.get("Type").getAsString();
        JsonElement element = jObject.get("Properties");

        try {
            return jsonDeserializationContext.deserialize(element, Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to deserialize component: " + clazz);
        }
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.add("Type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("Properties", jsonSerializationContext.serialize(component, component.getClass()));

        return result;
    }
}
