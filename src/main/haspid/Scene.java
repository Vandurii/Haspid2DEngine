package main.haspid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.ComponentSerializer;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Animation;
import main.components.stateMachine.Frame;
import main.components.stateMachine.StateMachine;
import main.physics.Physics2D;
import main.renderer.*;
import main.util.*;
import main.util.Properties;
import org.joml.Vector2d;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;

public abstract class Scene {

    private Gson gson;
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

         this.gson = new GsonBuilder().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                enableComplexMapKeySerialization().
                create();

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
        //  State Machine
        //===================================
        List<Frame> frameList;
        List<Animation> animList;
        double defaultFrameTime = 0.23;

        ArrayList<StateMachine> stateMachines = new ArrayList<>();
        SpriteSheet smallFormSheet = AssetPool.getSpriteSheet(smallFormConfig);
        SpriteSheet bigFormSheet = AssetPool.getSpriteSheet(bigFormConfig);
        SpriteSheet itemsSheet = AssetPool.getSpriteSheet(itemsConfig);
        SpriteSheet turtleSheet = AssetPool.getSpriteSheet(turtleConfig);

        // small Mario
        frameList = loadFrame(defaultFrameTime, smallFormSheet, 0, 2, 3, 2);
        Animation run = new Animation("run", true, frameList);

        frameList = loadFrame(0.2, smallFormSheet, 4);
        Animation switchDirection = new Animation("switch", false, frameList);

        frameList = loadFrame(1, smallFormSheet, 0);
        Animation idle  = new Animation("idle", true, frameList);

        frameList = loadFrame(0.1, smallFormSheet, 5);
        Animation jump = new Animation("jump", true, frameList);

        animList = Arrays.asList(run, switchDirection, idle, jump);
        StateMachine smallMario = new StateMachine("smallMario", "idle", animList);
        stateMachines.add(smallMario);


        // big Mario
        frameList = loadFrame(defaultFrameTime, bigFormSheet, 0, 1, 2, 3, 2, 1);
        Animation bigRun = new Animation("run", true, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 4);
        Animation bigSwitchDirection = new Animation("switch", false, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 0);
        Animation bigIdle = new Animation("idle", true, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 5);
        Animation bigJump = new Animation("jump", true, frameList);

        animList = Arrays.asList(bigRun, bigIdle, bigJump, bigSwitchDirection);
        StateMachine bigMario = new StateMachine("bigMario", "idle", animList);
        stateMachines.add(bigMario);


        // fire Mario
        frameList = loadFrame(defaultFrameTime, bigFormSheet, 21, 22, 23, 24, 23, 22);
        Animation fireRun = new Animation("run", true, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 25);
        Animation fireSwitchDirection = new Animation("switch", false, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 21);
        Animation fireIdle = new Animation("idle", true, frameList);

        frameList = loadFrame(0.1, bigFormSheet, 26);
        Animation fireJump = new Animation("jump", true, frameList);

        animList = Arrays.asList(fireRun, fireIdle, fireJump, fireSwitchDirection);
        StateMachine fireMario = new StateMachine("fireMario", "idle", animList);
        stateMachines.add(fireMario);


        // die Mario
        frameList = loadFrame(0.1, smallFormSheet, 6);
        Animation die = new Animation("die", true, frameList);

        animList = Arrays.asList(die);
        StateMachine dieMario = new StateMachine("dieMario", "die", animList);
        stateMachines.add(dieMario);


        // question block
        frameList = loadFrame(0.57, itemsSheet, 0);
        frameList.add(new Frame(itemsSheet.getSprite(1), 0.23));
        frameList.add(new Frame(itemsSheet.getSprite(2), 0.23));
        Animation active = new Animation("active", true, frameList);

        frameList = loadFrame(0.1, itemsSheet, 3);
        Animation inactive = new Animation("inactive", true, frameList);

        animList = Arrays.asList(active, inactive);
        StateMachine questionBlock = new StateMachine("questionBlock","active", animList);
        stateMachines.add(questionBlock);


        // coin flip
        frameList = loadFrame(0.57, itemsSheet, 7);
        frameList.add(new Frame(itemsSheet.getSprite(8), defaultFrameTime));
        frameList.add(new Frame(itemsSheet.getSprite(9), defaultFrameTime));
        Animation coinFlip = new Animation("coin", true, frameList);

        animList = Arrays.asList(coinFlip);
        StateMachine coin = new StateMachine("coin","coin", animList);
        stateMachines.add(coin);


        // goomba
        frameList = loadFrame(defaultFrameTime, smallFormSheet, 14, 15);
        Animation goombaWalk = new Animation("walk", true, frameList);

        frameList = loadFrame(0.1, smallFormSheet, 16);
        Animation goombaSquashed = new Animation("squashed", true, frameList);

        animList = Arrays.asList(goombaWalk, goombaSquashed);
        StateMachine goomba = new StateMachine("goomba","walk", animList);
        stateMachines.add(goomba);

        // trutle
        frameList = loadFrame(defaultFrameTime, turtleSheet, 0, 1);
        Animation turtleWalk = new Animation("walk", true, frameList);

        frameList = loadFrame(0.1, turtleSheet, 2, 3);
        Animation turtleSquashed = new Animation("squashed", true, frameList);

        animList = Arrays.asList(turtleWalk, turtleSquashed);
        StateMachine turtle = new StateMachine("turtle","walk", animList);
        stateMachines.add(turtle);

       // saveResources(stateMachinePath, stateMachines.toArray());

        //===================================
        //  shader
        //===================================
        AssetPool.getShader(defaultShaderPath);

        //===================================
        //  Sound
        //===================================
        AssetPool.getSound(mainTheme);
        AssetPool.getSound(breakBlock);
        AssetPool.getSound(bump);
       // AssetPool.getSound(coin);
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

        SpriteConfig[] configList = gsonReader(resourcePath, SpriteConfig[].class);

        for(SpriteConfig config: configList){
            propertiesList.add(AssetPool.getSpriteSheet(config));
        }

      //  propertiesList.add(AssetPool.getAllSound());
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
        if(gameObjects == null) return;
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

    public <T> void saveResources(String path, T[] resource){
       T[] array = (T[]) gsonReader(path, resource.getClass());

       if(array != null) {
           T[] concatenateArray = (T[]) Array.newInstance(array.getClass().getComponentType(), (array.length + resource.length));
           System.arraycopy(array, 0, concatenateArray, 0, array.length);
           System.arraycopy(resource, 0, concatenateArray, array.length, resource.length);

           System.out.println(Arrays.toString(concatenateArray) + "<<");
           gsonWriter(path, concatenateArray, false);
       }else{
           gsonWriter(path, resource, false);
       }
    }

    public <T extends Writable> T loadResources(String path, Class<T[]> resource, String name){
        T[] array = gsonReader(path, resource);

        for(T t: array){
            if(t.getName().equals(name)) return t;
        }

        return null;
    }

    public <T> void gsonWriter(String path, T[] objectsToSave, boolean append){
        try {
            FileWriter fileWriter = new FileWriter(path, append);
            String obj = gson.toJson(objectsToSave);
            fileWriter.write(obj);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public <T> T gsonReader(String path, Class<T> type){
        try{
            String data = new String(Files.readAllBytes(Paths.get(path)));
            if(!data.trim().isEmpty() && !data.trim().equals("[]")) {

                return gson.fromJson(data, type);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public void saveSceneToFile(){
        ArrayList<GameObject> list = new ArrayList<>();

        for(GameObject gameObject: sceneObjectList){
            if(gameObject.isSerializable()){
                list.add(gameObject);
            }
        }

        GameObject[] objectsToSave = (list.toArray(new GameObject[0]));

        gsonWriter(levelPath, objectsToSave, false);
    }

    public void loadSceneFromFile(){
        resetScene();

        GameObject[] gameObjects = gsonReader(levelPath, GameObject[].class);
        addObjectToSceneSafe(gameObjects);
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

    public SpriteSheet getProperties(String name){
        for(Properties prop: propertiesList){
            if(prop.getName().equals(name)){
                if(prop instanceof SpriteSheet){
                    return (SpriteSheet) prop;
                }else{
                    throw new IllegalStateException();
                }
            }
        }

        return null;
    }
}
