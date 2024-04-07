package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.haspid.ComponentSerializer;
import main.haspid.GameObject;
import main.haspid.GameObjectDeserializer;
import main.renderer.Renderer;
import main.util.AssetPool;

import javax.imageio.IIOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning;
    private static List<GameObject> sceneObjectList;
    private Renderer renderer;
    protected GameObject activeGameObject;

    public Scene(){
        loadResources();

        sceneObjectList = new ArrayList<>();
        renderer = new Renderer();
    }

    public abstract void update(float dt);

    public abstract void init();

    public void start(){
        for(GameObject gameObject: sceneObjectList){
            gameObject.start();
            renderer.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject){
        sceneObjectList.add(gameObject);

        if(isRunning){
            gameObject.start();
            renderer.add(gameObject);
        }
    }

    public void addGameObjectToScene(GameObject ...gameObjects){
        for(GameObject gameObject: gameObjects) {
            sceneObjectList.add(gameObject);

            if (isRunning) {
                gameObject.start();
                renderer.add(gameObject);
            }
        }
    }

    public Camera getCamera(){
        return camera;
    }

    public List<GameObject> getSceneObjectList(){
        return sceneObjectList;
    }

    public Renderer getRenderer(){
        return renderer;
    }

    private void loadResources(){
        AssetPool.getShader(defaultShaderPath);
        AssetPool.getSpriteSheet(firstSpriteSheet);
    }

    public void save(){
            Gson gson = new GsonBuilder().
                    setPrettyPrinting().
                    registerTypeAdapter(Component.class, new ComponentSerializer()).
                    registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                    create();

        try {
            FileWriter fileWriter = new FileWriter(levelPath);
            String obj = gson.toJson(sceneObjectList);
            fileWriter.write(obj);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void load(){
        Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                enableComplexMapKeySerialization().
                create();

        try{
            String data = new String(Files.readAllBytes(Paths.get(levelPath)));
            if(!data.trim().equals("")) {
                GameObject[] gameObjects = gson.fromJson(data, GameObject[].class);
                activeGameObject = gameObjects[0];
                addGameObjectToScene(gameObjects);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
