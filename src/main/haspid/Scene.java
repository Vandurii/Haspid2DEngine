package main.haspid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.ComponentSerializer;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Frame;
import main.components.stateMachine.StateMachine;
import main.physics.Physics2D;
import main.renderer.*;
import main.util.AssetPool;
import main.util.Properties;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;

public abstract class Scene {

    protected Camera camera;
    private Renderer renderer;
    protected Physics2D physics;
    protected boolean editorMode;
    private List<GameObject> sceneObjectList;
    private List<GameObject> addObjectQueue;
    private List<GameObject> removeObjectQueue;
    private ArrayList<Properties> propertiesList;
    private List<Properties> removePropertiesQueue;
    private Map<Component, GameObject> addComponentQueue;
    private Map<Component, GameObject> removeComponentQueue;
    private Map<GameObject, Vector2d> objectChangePosQueue;

    public SpriteSheet itemsSheet = AssetPool.getSpriteSheet(itemsConfig);
    public SpriteSheet smallFormSheet = AssetPool.getSpriteSheet(smallFormConfig);
    public SpriteSheet bigFormSheet = AssetPool.getSpriteSheet(bigFormConfig);
    public SpriteSheet turtleSheet = AssetPool.getSpriteSheet(turtleConfig);

    public Scene(){
        this.physics = new Physics2D();
        this.propertiesList = new ArrayList<>();
        this.renderer = Renderer.getInstance();
        this.addComponentQueue = new HashMap<>();
        this.sceneObjectList = new ArrayList<>();
        this.addObjectQueue = new ArrayList<>();
        this.removeComponentQueue = new HashMap<>();
        this.removeObjectQueue = new ArrayList<>();
        this.objectChangePosQueue = new HashMap<>();
        this.removePropertiesQueue = new ArrayList<>();
        this.camera = new Camera(new Vector2d(0, 0));

        Component.resetCounter();
        Renderer.resetInstance();
        loadResources();
    }

    public List<Frame> loadFrame(double frameTime, SpriteSheet spriteSheet, int ...index){
        List<Frame> frameList = new ArrayList<>();

        for(int i: index){
            frameList.add(new Frame(spriteSheet.getSprite(i), frameTime));
        }

        return frameList;
    }

    private void loadResources(){
        //===================================
        //  shader
        //===================================
        AssetPool.getShader(defaultShaderPath);

        //===================================
        //  spriteSheet
        //===================================
        AssetPool.getSpriteSheet(smallFormConfig);
        AssetPool.getSpriteSheet(decorationAndBlockConfig);
        AssetPool.getSpriteSheet(gizmosConfig);
        AssetPool.getSpriteSheet(itemsConfig);

        //===================================
        //  Sound
        //===================================
        AssetPool.getSound(mainTheme);
        AssetPool.getSound(breakBlock);
        AssetPool.getSound(bump);
        AssetPool.getSound(coin);
        AssetPool.getSound(gameOver);
        AssetPool.getSound(jumpSmall);
        AssetPool.getSound(marioDie);
        AssetPool.getSound(pipe);
        AssetPool.getSound(powerUp);
        AssetPool.getSound(powerUpAppears);
        AssetPool.getSound(stageClear);
        AssetPool.getSound(stomp);
        AssetPool.getSound(kick);
        AssetPool.getSound(invincible);

        //===================================
        //  Properties
        //===================================
        propertiesList.add(AssetPool.getSpriteSheet(itemsConfig));
        propertiesList.add(AssetPool.getSpriteSheet(smallFormConfig));
        propertiesList.add(AssetPool.getSpriteSheet(decorationAndBlockConfig));
        propertiesList.add(AssetPool.getSpriteSheet(pipesConfig));
        propertiesList.add(AssetPool.getSpriteSheet(turtleConfig));
        propertiesList.add(AssetPool.getSpriteSheet(iconConfig));
        propertiesList.add(AssetPool.getAllSound());

    }

    public abstract void init();

    public abstract void update(float dt);

    public abstract void render(float dt, boolean bufferIDMode);

    public abstract void destroy();

    public abstract void disposeDearGui();

    public void sceneUpdate(float dt){
        updateGameObject(dt);
        changePosition();

        removeComponentFromObject();
        addComponentToObject();

        removeDeadObject();
        addGameObjectToScene();

        removeProperties();

        physics.update(dt);
    }

    // safe method
    public void changePositionSafe(Vector2d pos, GameObject gameObject){
        objectChangePosQueue.put(gameObject, pos);
    }

    public void removeComponentSafe(GameObject gameObject, Component component){
        removeComponentQueue.put(component, gameObject);
    }

    public void addComponentSafe(GameObject gameObject, Component component){
        addComponentQueue.put(component, gameObject);
    }

    public void removeFromSceneSafe(GameObject gameObject){
        removeObjectQueue.add(gameObject);
    }

    public void addObjectToSceneSafe(GameObject ...gameObjects){
        Collections.addAll(addObjectQueue, gameObjects);
    }

    public void removeFromSceneUnsafe(GameObject gameObject){
        sceneObjectList.remove(gameObject);
    }

    public void removePropertiesSafe(Properties properties){
        removePropertiesQueue.add(properties);
    }

    private void updateGameObject(float dt){
        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }
    }

    private void changePosition(){
        Console.addLog(new Log(INFO, String.format("%s object in queue.", objectChangePosQueue.size())));
        for(Map.Entry<GameObject, Vector2d> entry: objectChangePosQueue.entrySet()) {
            // set new value here and in box2D
            entry.getKey().getTransform().setPosition(entry.getValue());
            entry.getKey().getComponent(RigidBody.class).setPosition(entry.getValue());
        }

        objectChangePosQueue.clear();
    }

    private void removeComponentFromObject() {
        Console.addLog(new Log(INFO, String.format("%s in queue.", removeComponentQueue.size())));
        for (Map.Entry<Component, GameObject> entry : removeComponentQueue.entrySet()) {
            Component c = entry.getKey();
            GameObject go = entry.getValue();

            // remove it from render if it implement Drawable interface
            if(c instanceof Drawable){
                ((Drawable) c ).setDirty(true);
            }
            go.removeComponent(c);

            Console.addLog(new Log(INFO, String.format("Deleted: %s from %s ID:%s", c, go, go.getGameObjectID())));
        }
        removeComponentQueue.clear();
    }

    private void addComponentToObject() {
        Console.addLog(new Log(INFO, String.format("%s component in queue.", addComponentQueue.size())));

        for (Map.Entry<Component, GameObject> entry : addComponentQueue.entrySet()) {
            entry.getValue().addComponent(entry.getKey());
        }

        // Clear queue
        addComponentQueue.clear();
    }

    private void removeDeadObject(){
        Console.addLog(new Log(INFO, String.format("%s object in queue.", removeObjectQueue.size())));
        for(GameObject gameObject: removeObjectQueue){
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

            // remove sprite data from render
            if(spriteRenderer != null){
                spriteRenderer.markToRemove();
            }

            // remove collider line data from render
            List<Line2D> lineList = gameObject.getAllCompThisType(Line2D.class);
            if(!lineList.isEmpty()) {
                for (Line2D line : lineList) {
                    line.markToRemove(true);
                }
            }

            // remove object date from physic engine
            physics.destroyGameObject(gameObject);

            // remove object form scene list
            sceneObjectList.remove(gameObject);
        }

        removeObjectQueue.clear();
    }

    private void addGameObjectToScene(){
        Console.addLog(new Log(INFO, String.format("%s object in queue.", addObjectQueue.size())));

        for(GameObject gameObject: addObjectQueue) {
            sceneObjectList.add(gameObject);
            physics.add(gameObject);

            // init it and add to Render
            gameObject.init();
            renderer.add(gameObject);
        }

        addObjectQueue.clear();
    }

    private void removeProperties(){
        for(Properties prop: removePropertiesQueue){
            propertiesList.remove(prop);
        }

        removePropertiesQueue.clear();
    }

    public void saveStateMachine(String path, List<StateMachine> stateMachineList){
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                enableComplexMapKeySerialization().
                create();

        try {
            FileWriter fileWriter = new FileWriter(path);

            String obj = gson.toJson(stateMachineList);
            fileWriter.write(obj);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public StateMachine loadStateMachine(String path, String name){
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                create();

        try{
            String data = new String(Files.readAllBytes(Paths.get(path)));
            if(!data.trim().isEmpty() && !data.trim().equals("[]")) {
                StateMachine[] stateMachines = gson.fromJson(data, StateMachine[].class);

                for(StateMachine stateMachine: stateMachines){
                    if(stateMachine.getName().equals(name)) return stateMachine;
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public void saveSceneToFile(){
            Gson gson = new GsonBuilder().
                    registerTypeAdapter(Component.class, new ComponentSerializer()).
                    enableComplexMapKeySerialization().
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

    public void loadSceneFromFile(){
        resetScene();

        Gson gson = new GsonBuilder().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                create();

        try{
            String data = new String(Files.readAllBytes(Paths.get(levelPath)));
            if(!data.trim().isEmpty() && !data.trim().equals("[]")) {
                GameObject[] gameObjects = gson.fromJson(data, GameObject[].class);
                addObjectToSceneSafe(gameObjects);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetScene(){
        sceneObjectList.clear();
        Component.resetCounter();
        Renderer.resetInstance();
    }

    public boolean isInEditMode(){
        return editorMode;
    }

    public GameObject getObjectByID(int id){
        for(GameObject object: sceneObjectList){
            if(object.getGameObjectID() == id){
                // if object is not triggerable then return null
                if(!object.isTriggerable()) break;

                return object;
            }
        }

        return null;
    }

    public GameObject getObjectByName(String name){
        for(GameObject object: sceneObjectList){
            if(object.getName().equals(name)) return object;
        }

        return null;
    }

    public Physics2D getPhysics(){
        return physics;
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

    public List<Properties> getProperties(){
        return  propertiesList;
    }
}
