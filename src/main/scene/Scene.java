package main.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Editor.InspectorWindow;
import main.components.Component;
import main.components.SpriteRenderer;
import main.haspid.Camera;
import main.components.ComponentSerializer;
import main.haspid.GameObject;
import main.haspid.GameObjectDeserializer;
import main.haspid.Window;
import main.renderer.Renderer;
import main.util.AssetPool;
import main.util.Texture;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning;
    private static List<GameObject> sceneObjectList;
    private Renderer renderer;

    public Scene(){
        Component.resetCounter();
        loadResources();

        sceneObjectList = new ArrayList<>();
        renderer = Renderer.getInstance();
    }

    public abstract void update(float dt);

    public abstract void init();

    public abstract void end();

    public abstract void render(float dt, boolean bufferIDMode);

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

    public void addGameObjectToScene(GameObject ...gameObjects){;
        for(GameObject gameObject: gameObjects) {

            sceneObjectList.add(gameObject);

            if (isRunning) {
                gameObject.start();
                renderer.add(gameObject);
            }
        }
    }

    public void removeFromScene(GameObject gameObject){
        sceneObjectList.remove(gameObject);
        if(gameObject.getComponent(SpriteRenderer.class) != null) gameObject.getComponent(SpriteRenderer.class).markToRemove();
    }

    public GameObject getGameObjectFromID(int id){
        for(GameObject go: sceneObjectList){
            if(go.getGameObjectID() == id) return go;
        }

        return null;
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
            ArrayList<GameObject> objectsToSave = new ArrayList<>();

            for(GameObject gameObject: sceneObjectList){
                if(gameObject.isSerializable()){
                    objectsToSave.add(gameObject);
                }
            }

            String obj = gson.toJson(objectsToSave);
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
            if(!data.trim().isEmpty() && !data.trim().equals("[]")) {
                GameObject[] gameObjects = gson.fromJson(data, GameObject[].class);
                addGameObjectToScene(gameObjects);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void printSceneObjects(){
        List<GameObject> goList = new ArrayList<>();
        List<Component> cList = new ArrayList<>();
        List<SpriteRenderer> sList = new ArrayList<>();
        List<Texture> tList = new ArrayList<>();

        System.out.println("*********************");
        System.out.println("Object in Scene");
        System.out.println("*********************");
        for(GameObject gameObject: sceneObjectList){
            if(!goList.contains(gameObject)) goList.add(gameObject);
            for(Component component: gameObject.getAllComponent()){
                if(!cList.contains(component)) cList.add(component);

                if(component instanceof SpriteRenderer){
                    SpriteRenderer spriteRenderer = (SpriteRenderer) component;
                    if(!sList.contains(spriteRenderer))sList.add(spriteRenderer);
                    if(!tList.contains(spriteRenderer.getTexture()))tList.add(spriteRenderer.getTexture());
                }
            }
        }

        System.out.println("Objects : " + goList.size());
        System.out.println("component : " + goList.size());
        System.out.println("sprites : " + goList.size());
        System.out.println("textures : " + goList.size());
        System.out.println("\n\n\n\n\n");
    }

}
