package main.util;

import java.util.HashMap;
import java.util.Map;

public class AssetPool{
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheetList = new HashMap<>();

    public static Shader getShader(String resourceName){
        if(!shaders.containsKey(resourceName)){
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(resourceName, shader);
        }

            return shaders.get(resourceName);
    }

    public static Texture getTexture(String resourceName){
        if(!textures.containsKey(resourceName)){
            textures.put(resourceName, new Texture(resourceName));
        }

        return textures.get(resourceName);
    }

    public static SpriteSheet getSpriteSheet(SpriteConfig config){
        String resourceName = config.filePath;
        if(!spriteSheetList.containsKey(resourceName)){
            config.texture = getTexture(config.filePath);
            SpriteSheet spriteSheet = new SpriteSheet(config);
            spriteSheetList.put(resourceName, spriteSheet);
        }

            return spriteSheetList.get(resourceName);
    }
}
