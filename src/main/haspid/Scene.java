package main.haspid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.ComponentSerializer;
import main.components.physicsComponent.RigidBody;
import main.components.stateMachine.Animation;
import main.components.stateMachine.StateMachine;
import main.physics.Physics2D;
import main.renderer.DebugDraw;
import main.renderer.DebugDrawEvents;
import main.renderer.Line2D;
import main.renderer.Renderer;
import main.util.AssetPool;
import main.util.SpriteSheet;
import org.joml.Vector2d;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

public abstract class Scene {

    protected Camera camera;
    private Renderer renderer;
    protected Physics2D physics;
    protected boolean editorMode;
    private List<GameObject> sceneObjectList;
    private List<GameObject> addObjectQueue;
    private List<GameObject> removeObjectQueue;
    private Map<Component, GameObject> addComponentQueue;
    private Map<Component, GameObject> removeComponentQueue;
    private Map<GameObject, Vector2d> objectChangePosQueue;

    public SpriteSheet itemsSheet = AssetPool.getSpriteSheet(itemsConfig);
    public SpriteSheet smallFormSheet = AssetPool.getSpriteSheet(smallFormConfig);
    public SpriteSheet bigFormSheet = AssetPool.getSpriteSheet(bigFormConfig);
    public SpriteSheet turtleSheet = AssetPool.getSpriteSheet(turtleConfig);

    public Scene(){
        this.physics = new Physics2D();
        this.renderer = Renderer.getInstance();
        this.addComponentQueue = new HashMap<>();
        this.sceneObjectList = new ArrayList<>();
        this.addObjectQueue = new ArrayList<>();
        this.removeComponentQueue = new HashMap<>();
        this.removeObjectQueue = new ArrayList<>();
        this.objectChangePosQueue = new HashMap<>();
        this.camera = new Camera(new Vector2d(0, 0));

        Component.resetCounter();
        Renderer.resetInstance();
        loadResources();
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
        //  State Machine
        //===================================
        double defaultFrameTime = 0.23;


        // small Mario
        StateMachine smallMario = new StateMachine("idle");

        Animation run = new Animation("run", true);
        run.addFrame(smallFormSheet.getSprite(0), defaultFrameTime);
        run.addFrame(smallFormSheet.getSprite(2), defaultFrameTime);
        run.addFrame(smallFormSheet.getSprite(3), defaultFrameTime);
        run.addFrame(smallFormSheet.getSprite(2), defaultFrameTime);

        Animation switchDirection = new Animation("switch", false);
        switchDirection.addFrame(smallFormSheet.getSprite(4), 0.2);

        Animation idle  = new Animation("idle", true);
        idle.addFrame(smallFormSheet.getSprite(0), 1);

        Animation jump = new Animation("jump", true);
        jump.addFrame(smallFormSheet.getSprite(5), 0.1);

        smallMario.addState(idle, switchDirection, run, jump);
        AssetPool.putStateMachine("smallMario", smallMario);


        // big Mario
        StateMachine bigMario = new StateMachine("idle");

        Animation bigRun = new Animation("run", true);
        bigRun.addFrame(bigFormSheet.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigFormSheet.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigFormSheet.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigFormSheet.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigFormSheet.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigFormSheet.getSprite(1), defaultFrameTime);

        Animation bigSwitchDirection = new Animation("switch", false);
        bigSwitchDirection.addFrame(bigFormSheet.getSprite(4), 0.1f);

        Animation bigIdle = new Animation("idle", true);
        bigIdle.addFrame(bigFormSheet.getSprite(0), 0.1f);

        Animation bigJump = new Animation("jump", true);
        bigJump.addFrame(bigFormSheet.getSprite(5), 0.1f);

        bigMario.addState(bigRun, bigIdle, bigJump, bigSwitchDirection);
        AssetPool.putStateMachine("bigMario", bigMario);


        // fire Mario
        StateMachine fireMario = new StateMachine("idle");

        Animation fireRun = new Animation("run", true);
        fireRun.addFrame(bigFormSheet.getSprite(21), defaultFrameTime);
        fireRun.addFrame(bigFormSheet.getSprite(22), defaultFrameTime);
        fireRun.addFrame(bigFormSheet.getSprite(23), defaultFrameTime);
        fireRun.addFrame(bigFormSheet.getSprite(24), defaultFrameTime);
        fireRun.addFrame(bigFormSheet.getSprite(23), defaultFrameTime);
        fireRun.addFrame(bigFormSheet.getSprite(22), defaultFrameTime);

        Animation fireSwitchDirection = new Animation("switch", false);
        fireSwitchDirection.addFrame(bigFormSheet.getSprite(25), 0.1f);

        Animation fireIdle = new Animation("idle", true);
        fireIdle.addFrame(bigFormSheet.getSprite(21), 0.1f);

        Animation fireJump = new Animation("jump", true);
        fireJump.addFrame(bigFormSheet.getSprite(26), 0.1f);

        fireMario.addState(fireRun, fireIdle, fireJump, fireSwitchDirection);
        AssetPool.putStateMachine("fireMario", fireMario);

        // die Mario
        StateMachine dieMario = new StateMachine("die");
        Animation die = new Animation("die", true);
        die.addFrame(smallFormSheet.getSprite(6), 0.1);
        dieMario.addState(die);
        AssetPool.putStateMachine("dieMario", dieMario);


        // question block
        StateMachine questionBlock = new StateMachine("active");

        Animation active = new Animation("active", true);
        active.addFrame(itemsSheet.getSprite(0), 0.57f);
        active.addFrame(itemsSheet.getSprite(1), 0.23f);
        active.addFrame(itemsSheet.getSprite(2), 0.23f);

        Animation inactive = new Animation("inactive", true);
        inactive.addFrame(itemsSheet.getSprite(3), 0.1);

        questionBlock.addState(active, inactive);
        AssetPool.putStateMachine("questionBlock", questionBlock);


        // coin flip
        StateMachine coin = new StateMachine("coin");
        Animation coinFlip = new Animation("coin", true);
        coinFlip.addFrame(itemsSheet.getSprite(7), 0.57f);
        coinFlip.addFrame(itemsSheet.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(itemsSheet.getSprite(9), defaultFrameTime);
        coin.addState(coinFlip);
        AssetPool.putStateMachine("coin", coin);


        // goomba
        StateMachine goomba = new StateMachine("walk");

        Animation goombaWalk = new Animation("walk", true);
        goombaWalk.addFrame(smallFormSheet.getSprite(14), defaultFrameTime);
        goombaWalk.addFrame(smallFormSheet.getSprite(15), defaultFrameTime);

        Animation goombaSquashed = new Animation("squashed", true);
        goombaSquashed.addFrame(smallFormSheet.getSprite(16), 0.1f);

        goomba.addState(goombaWalk, goombaSquashed);
        AssetPool.putStateMachine("goomba", goomba);

        // trutle
        StateMachine turtle = new StateMachine("walk");

        Animation turtleWalk = new Animation("walk", true);
        turtleWalk.addFrame(turtleSheet.getSprite(0), defaultFrameTime);
        turtleWalk.addFrame(turtleSheet.getSprite(1), defaultFrameTime);

        Animation turtleSquashed = new Animation("squashed", true);
        turtleSquashed.addFrame(turtleSheet.getSprite(2), 0.1f);
        turtleSquashed.addFrame(turtleSheet.getSprite(3), 0.1f);

        turtle.addState(turtleWalk, turtleSquashed);
        AssetPool.putStateMachine("turtle", turtle);
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
                // refresh line render after
                DebugDraw.notify(DebugDrawEvents.Garbage, colliderID);
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

    public void saveSceneToFile(){
            Gson gson = new GsonBuilder().
                    setPrettyPrinting().
                    registerTypeAdapter(Component.class, new ComponentSerializer()).
                    registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
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
                setPrettyPrinting().
                registerTypeAdapter(Component.class, new ComponentSerializer()).
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                enableComplexMapKeySerialization().
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
}
