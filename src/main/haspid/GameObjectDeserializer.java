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
        JsonArray components = jsonObject.getAsJsonArray("componentList");

        GameObject gameObject = new GameObject(name);
        for(JsonElement c: components){
            Component component = jsonDeserializationContext.deserialize(c, Component.class);
            gameObject.addComponent(component);
            if(component instanceof SpriteRenderer){
                ((SpriteRenderer) component).setDirty();
            }
        }

        gameObject.setTransformFromItself();

        return gameObject;
    }
}
