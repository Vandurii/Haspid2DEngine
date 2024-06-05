package main.haspid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.components.Component;
import main.components.SpriteRenderer;
import main.components.ComponentSerializer;
import main.components.physicsComponent.BoxCollider;
import main.components.physicsComponent.RigidBody;
import main.physics.BodyType;
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
                 setPrettyPrinting().
                registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).
                enableComplexMapKeySerialization().
                create();

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
        // Properties
        // ===================================

        SpriteConfig[] configList = gsonReader(resourcePath, SpriteConfig[].class);

        if(configList != null) {
            for (SpriteConfig config : configList) {
                propertiesList.add(AssetPool.getSpriteSheet(config));
            }
        }

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

            //remove data from colliderList
            BoxCollider boxCollider = gameObject.getComponent(BoxCollider.class);
            if(boxCollider != null){
                BoxCollider.removeFromStaticCollierList(boxCollider);
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

            RigidBody rigidBody = gameObject.getComponent(RigidBody.class);
            if(rigidBody != null && rigidBody.getBodyType() != BodyType.Static) {
                physics.add(gameObject);
            }

            // init it and add to Render
            gameObject.init();
            renderer.add(gameObject);
        }

        addObjectQueue.clear();
    }

    private void removeProperties(){
        for(Properties prop: removePropertiesQueue){
            propertiesList.remove(prop);
            SpriteConfig[] table = {findConfig(prop)};
            if(table[0] == null){
                System.out.println("can't find config: " + prop.getName());
                removePropertiesQueue.clear();
                return;
            }
            removeResource(resourcePath, table);
        }

        removePropertiesQueue.clear();
    }

    public <T extends Writable> void saveResources(String path, T[] resource){
       T[] array = (T[]) gsonReader(path, resource.getClass());

       if(array != null) {
           int duplicate = 0;
           for(int i = 0; i < resource.length; i++){
               for(T t: array){
                   if(resource[i] != null && t.getName().equals(resource[i].getName())){
                       resource[i] = null;
                       duplicate++;
                       break;
                   }
               }
           }

           T[] concatenateArray = (T[]) Array.newInstance(array.getClass().getComponentType(), (array.length + resource.length - duplicate));
          // copy old files to new array
           System.arraycopy(array, 0, concatenateArray, 0, array.length);

           // copy new files to new array, skip null value, these are overwritten because were duplicate
           int l = array.length;
           for(int i = 0, j = 0; i < resource.length; i++){
               if(resource[i] != null){
                   concatenateArray[l + j] = resource[i];
                   j++;
               }
           }

           // check if none of these is null
           for(T t: concatenateArray){
               if(t == null){
                   throw new IllegalStateException("This value can't be null");
               }
           }

           gsonWriter(path, concatenateArray, false);
       }else{

           // check if none of these is null
           for(T t: resource){
               if(t == null) throw new IllegalStateException("This value can't be null");
           }

           gsonWriter(path, resource, false);
       }
    }

    public <T extends Writable> void removeResource(String path, T[] objectsToRemove){
        T[] array = (T[]) gsonReader(path, objectsToRemove.getClass());

        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), (array.length - objectsToRemove.length));
        for(int o = 0, n = 0; o < array.length; o++){

            boolean found = false;
            for(int r = 0; r < objectsToRemove.length; r++){

                T obj = objectsToRemove[r];
                if(obj != null && obj.getName().equals(array[o].getName())){
                    found = true;

                    objectsToRemove[r] = null;
                }
            }

            if(!found){
                newArray[n] = array[o];
                n++;
            }
        }

        gsonWriter(path, newArray, false);
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

    public SpriteConfig findConfig(Properties properties){
        SpriteConfig[] configList = gsonReader(resourcePath, SpriteConfig[].class);

        if(configList != null) {
            for (SpriteConfig config : configList) {
                if(properties.getName().equals(config.getName())) return config;
            }
        }

        return  null;
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

    public void addProperties(Properties properties){
        propertiesList.add(properties);
    }

    public List<SpriteSheet> getSpriteSheetFromProperties(){
        List<SpriteSheet> spriteSheetList = new ArrayList<>();
        for(Properties properties: propertiesList){
            if(properties instanceof SpriteSheet){
                spriteSheetList.add((SpriteSheet) properties);
            }
        }

        return  spriteSheetList;
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
