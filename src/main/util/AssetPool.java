package main.util;

import main.components.stateMachine.StateMachine;
import main.haspid.Window;

import java.util.*;

import static main.Configuration.stateMachinePath;

public class AssetPool{
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Sound> soundList = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, StateMachine> stateMachineMap = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheetList = new HashMap<>();

    public static Shader getShader(String resourceName){
        if(!shaders.containsKey(resourceName)){
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(resourceName, shader);
        }

            return shaders.get(resourceName);
    }

    public static Texture getTexture(String resourceName, boolean flip){
        if(!textures.containsKey(resourceName)){
            textures.put(resourceName, new Texture(resourceName, flip));
        }

        return textures.get(resourceName);
    }

    public static SpriteSheet getSpriteSheet(SpriteConfig config){
        String resourceName = config.filePath;
        if(!spriteSheetList.containsKey(resourceName)){
            config.texture = getTexture(config.filePath, config.flip);
            SpriteSheet spriteSheet = new SpriteSheet(config);
            spriteSheetList.put(resourceName, spriteSheet);
        }

            return spriteSheetList.get(resourceName);
    }

    public static Sound getSound(AudioConfig audioConfig){
        String resourceName = audioConfig.getFilePath();
        if(!soundList.containsKey(resourceName)){
            Sound sound = new Sound(audioConfig);
            soundList.put(resourceName, sound);
        }

        return soundList.get(resourceName);
    }

    // todo
    public static AudioSheet getAllSound(){
        AudioSheet audioSheet = new AudioSheet();
        for(Sound sound: soundList.values()){
            audioSheet.add(sound);
        }

        return audioSheet;
    }

    public static StateMachine getStateMachine(String name){
        if(!stateMachineMap.containsKey(name)){
            StateMachine stateMachine = Window.getInstance().getCurrentScene().loadResources(stateMachinePath, StateMachine[].class, name);
            stateMachine.init();
            stateMachineMap.put(stateMachine.getName(), stateMachine);
        }

        return stateMachineMap.get(name);
    }

    public static Set<String> getAllStatesMachines(){
        return stateMachineMap.keySet();
    }

    public static Collection<SpriteSheet> getAllSpriteSheet(){
        return spriteSheetList.values();
    }
}
