package main.util;

import main.SpriteConfig;
import main.components.SpriteSheet;
import main.renderer.Shader;
import main.renderer.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheetList = new HashMap<>();

    public static Shader getShader(String resourceName){
        if(!shaders.containsKey(resourceName)){
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(resourceName, shader);

            return shaders.get(resourceName);
        }else{
            return shaders.get(resourceName);
        }
    }

    public static Texture getTexture(String resourceName){
        if(!textures.containsKey(resourceName)){
            Texture texture = new Texture(resourceName);
            textures.put(resourceName, texture);

            return textures.get(resourceName);
        }else{
            return textures.get(resourceName);
        }
    }

    public static SpriteSheet getSpriteSheet(SpriteConfig config){
        String resourceName = config.filePath;
        if(!spriteSheetList.containsKey(resourceName)){
            config.texture = getTexture(config.filePath);
            SpriteSheet spriteSheet = new SpriteSheet(config);
            spriteSheetList.put(resourceName, spriteSheet);

            return spriteSheetList.get(resourceName);
        }else{
            return spriteSheetList.get(resourceName);
        }
    }

}
