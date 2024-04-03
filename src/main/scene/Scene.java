package main.scene;

import main.haspid.Camera;
import main.haspid.GameObject;
import main.renderer.Renderer;
import main.util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static main.Configuration.*;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning;
    private static List<GameObject> sceneObjectList;
    private Renderer renderer;

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
    }
}
