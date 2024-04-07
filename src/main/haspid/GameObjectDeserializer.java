package main.haspid;

import com.google.gson.*;
import main.components.Component;
import main.components.SpriteRenderer;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = jsonDeserializationContext.deserialize(jsonObject.get("zIndex"), int.class);
        JsonArray components = jsonObject.getAsJsonArray("componentList");

        GameObject gameObject = new GameObject(name, transform, zIndex);
        for(JsonElement c: components){
            Component component = jsonDeserializationContext.deserialize(c, Component.class);
            gameObject.addComponent(component);
            if(component instanceof SpriteRenderer) ((SpriteRenderer) component).getSprite().setDirty();
        }

        return gameObject;
    }
}
