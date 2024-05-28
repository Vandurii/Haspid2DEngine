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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.Configuration.*;
import static main.haspid.Log.LogType.INFO;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning;
    private Renderer renderer;
    protected Physics2D physics;
    protected boolean editorMode;
    private List<GameObject> sceneObjectList;
    private List<GameObject> pendingObjectList;
    private List<GameObject> objectToRemoveList;
    private Map<Component, GameObject> componentToAddMap;
    private Map<Component, GameObject> componentToRemoveMap;
    private Map<GameObject, Vector2d> objectToChangePositionMap;

    public SpriteSheet itemsSheet = AssetPool.getSpriteSheet(itemsConfig);
    public SpriteSheet smallFormSheet = AssetPool.getSpriteSheet(smallFormConfig);
    public SpriteSheet bigFormSheet = AssetPool.getSpriteSheet(bigFormConfig);
    public SpriteSheet turtleSheet = AssetPool.getSpriteSheet(turtleConfig);

    public Scene(){
        this.physics = new Physics2D();
        this.renderer = Renderer.getInstance();
        this.componentToAddMap = new HashMap<>();
        this.sceneObjectList = new ArrayList<>();
        this.pendingObjectList = new ArrayList<>();
        this.componentToRemoveMap = new HashMap<>();
        this.objectToRemoveList = new ArrayList<>();
        this.objectToChangePositionMap = new HashMap<>();
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

    public void start(){
        for(GameObject gameObject: sceneObjectList){
            gameObject.start();
            renderer.add(gameObject);
        }
        isRunning = true;
    }

    public void runTimeUpdate(float dt){
        updateGameObject(dt);

        removeComponentFromObject();
        addComponentToObject();

        removeDeadObject();
        addPendingObject();

        changePosition();
        physics.update(dt);
    }

    public void updateGameObject(float dt){
        for (GameObject go : getSceneObjectList()) {
            go.update(dt);
        }
    }

    public void addGameObjectToScene(GameObject ...gameObjects){;
        for(GameObject gameObject: gameObjects) {
            sceneObjectList.add(gameObject);
            physics.add(gameObject);

            if (isRunning) {
                gameObject.start();
                renderer.add(gameObject);
            }
        }
    }

    public void addObjectToSceneRunTime(GameObject gameObject){
        pendingObjectList.add(gameObject);
    }

    private void addPendingObject(){
        for(GameObject gameObject: pendingObjectList){
            addGameObjectToScene(gameObject);
        }

        pendingObjectList.clear();
    }

    public void addComponentRuntime(GameObject gameObject, Component component){
        Console.addLog(new Log(INFO, "addComponentRuntime:: " + component.getClass().getSimpleName()));
        componentToAddMap.put(component, gameObject);
    }

    protected void addComponentToObject() {

        if(!componentToAddMap.isEmpty()){
        Console.addLog(new Log(INFO, "addComponentToObject:: " + componentToAddMap.size()));
        }

        for (Map.Entry<Component, GameObject> entry : componentToAddMap.entrySet()) {
            entry.getValue().addComponent(entry.getKey());
        }

        // Clear list after all component are added.
        componentToAddMap.clear();
    }

    public void changePositionRuntime(Vector2d pos, GameObject gameObject){
        objectToChangePositionMap.put(gameObject, pos);
    }

    private void changePosition(){
        for(Map.Entry<GameObject, Vector2d> entry: objectToChangePositionMap.entrySet()) {
            entry.getKey().getTransform().setPosition(entry.getValue());
            entry.getKey().getComponent(RigidBody.class).setPosition(entry.getValue());
        }

        objectToChangePositionMap.clear();
    }

    public void removeFromScene(GameObject gameObject){
        if(gameObject != null && gameObject.getComponent(SpriteRenderer.class) != null){
            gameObject.getComponent(SpriteRenderer.class).markToRemove();
            physics.destroyGameObject(gameObject);
            sceneObjectList.remove(gameObject);

            List<Line2D> lineList = gameObject.getAllCompThisType(Line2D.class);
            if(!lineList.isEmpty()) {
                for (Line2D line : lineList) {
                    line.markToRemove(true);
                }

                DebugDraw.notify(DebugDrawEvents.Garbage, colliderID);
            }
        }
    }

    public void removeFromSceneRuntime(GameObject gameObject){
        objectToRemoveList.add(gameObject);
    }

    private void removeDeadObject(){
        for(GameObject gameObject: objectToRemoveList){
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
            if(spriteRenderer != null){
                spriteRenderer.markToRemove();
            }

            physics.destroyGameObject(gameObject);
            sceneObjectList.remove(gameObject);
        }

        objectToRemoveList.clear();
    }

    public void removeComponentRuntime(GameObject gameObject, Component component){
        componentToRemoveMap.put(component, gameObject);
    }

    protected void removeComponentFromObject() {
        for (Map.Entry<Component, GameObject> entry : componentToRemoveMap.entrySet()) {
            entry.getValue().removeComponent(entry.getKey());
            Console.addLog(new Log(INFO, "RemoveComponentFromObject:: " + entry.getKey().getClass().getSimpleName()));
        }
        componentToRemoveMap.clear();
    }
    
    public void saveSceneObject(){
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

    public void loadSceneObject(){
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
                addGameObjectToScene(gameObjects);
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
                if(!object.isTriggerable()){
                    break;
                }

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
