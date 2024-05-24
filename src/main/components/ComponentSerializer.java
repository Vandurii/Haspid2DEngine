package main.components;

import com.google.gson.*;
import jdk.dynalink.linker.LinkerServices;
import main.components.Component;
import main.util.AssetPool;

import java.lang.reflect.Type;

public class ComponentSerializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jObject = jsonElement.getAsJsonObject();
        String clazz = jObject.get("Type").getAsString();
        JsonElement element = jObject.get("Properties");

        try {
            Component component = jsonDeserializationContext.deserialize(element, Class.forName(clazz));
            component.updateIDCounter();

            if(component instanceof  SpriteRenderer && ((SpriteRenderer)component).hasTexture()){
                SpriteRenderer spriteRenderer = (SpriteRenderer) component;
                String filePath = spriteRenderer.getTexture().getFilePath();
                boolean flip = spriteRenderer.getTexture().isFlipped();
                spriteRenderer.setTexture(AssetPool.getTexture(filePath, flip));
            }

            return component;
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
